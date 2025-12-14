package dataAccess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import domain.Driver;
import domain.Ride;
import domain.Traveler;
import domain.User;
import configuration.JPAUtil;
import configuration.UtilDate;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.ErabiltzaileaDagoenekoErregistratutaException;

public class DataAccess {
	private EntityManager db;

	public void open() {
		db = JPAUtil.getEntityManager();
	}

	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	public List<String> getDepartCities() {
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.departing FROM Ride r ORDER BY r.departing", String.class);
		return query.getResultList();
	}

	public List<String> getArrivalCities(String departing) {
		TypedQuery<String> query = db.createQuery(
				"SELECT DISTINCT r.arrival FROM Ride r WHERE r.departing=:from ORDER BY r.arrival",
				String.class);
		query.setParameter("from", departing);
		return query.getResultList();
	}

	public List<Ride> getRides(String departing, String arrival, Date rideDate) {
	    if (departing == null || arrival == null || rideDate == null)
	        return new ArrayList<Ride>();
	   
	    Calendar calendar = Calendar.getInstance();
	    
	    
	    calendar.setTime(rideDate);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    Date startDate = calendar.getTime();
	    
	    
	    calendar.setTime(rideDate);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    Date endDate = calendar.getTime();
	    
	    try {
	     
	        TypedQuery<Ride> query = db.createQuery(
	            "SELECT r FROM Ride r WHERE r.departing=:departing AND r.arrival=:arrival AND r.date >= :startDate AND r.date <= :endDate",
	            Ride.class);
	        
	        return query
	            .setParameter("departing", departing)
	            .setParameter("arrival", arrival)
	            .setParameter("startDate", startDate) 
	            .setParameter("endDate", endDate) // <--- ¡Línea corregida y añadida!
	            .getResultList();
	            
	    } catch (Exception e) {
	        System.out.println("Error en getRidesByValues: " + e.getMessage());
	        e.printStackTrace();
	        return new ArrayList<Ride>();
	    }
	}

	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
	    
	    Date startDate = UtilDate.firstDayMonth(date);
	    Date endDate = UtilDate.lastDayMonth(date);

	    String jpql = "SELECT DISTINCT r.date FROM Ride r " +
	                  "WHERE r.departing = :fromCity " +
	                  "AND r.arrival = :toCity " +
	                  "AND r.date BETWEEN :startDate AND :endDate";
	    
	    TypedQuery<Date> query = db.createQuery(jpql, Date.class);
	    query.setParameter("fromCity", from);
	    query.setParameter("toCity", to);
	    query.setParameter("startDate", startDate);
	    query.setParameter("endDate", endDate);

	    return query.getResultList();
	}

	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
	        throws RideAlreadyExistException, RideMustBeLaterThanTodayException {
	    
	    if (from == null || to == null || date == null || nPlaces <= 0 || price < 0 || driverEmail == null)
	        return null;
	    
	    if (new Date().compareTo(date) > 0) {
	        throw new RideMustBeLaterThanTodayException("Ride date must be later than today");
	    }
	    
	    db.getTransaction().begin();
	
	    TypedQuery<Ride> query = db.createQuery(
	        "SELECT r FROM Ride r WHERE r.departing=:from_city AND r.arrival=:to_city AND r.date=:date", Ride.class);
	    
	    query.setParameter("from_city", from);
	    query.setParameter("to_city", to);
	    query.setParameter("date", date);
	    
	    if (!query.getResultList().isEmpty()) {
	        db.getTransaction().rollback();
	    
	        throw new RideAlreadyExistException("Ya existe un viaje con esta ruta y fecha.");
	    }
	  

	    Driver driver = db.find(Driver.class, driverEmail);
	    if (driver == null) {
	        System.out.println("Driver ez da aurkitu");
	        db.getTransaction().rollback();
	        return null;
	    }

	   
	    
	    Ride ride = driver.addRide(from, to, date, price, nPlaces);
	    
	   
	    db.persist(driver); 
	    db.getTransaction().commit();
	    
	    System.out.println("✓ BIDAIA SORTUTA: " + from + " → " + to + " (" + date + ") - " + price + "€");
	    
	    return ride;
	}

	// En DataAccess.java
	public User register(String email, String name, String surname, String password, boolean isDriver) throws ErabiltzaileaDagoenekoErregistratutaException {
	    if (email == null || name == null || password == null)
	        return null;
	    try {
	        
	      
	        TypedQuery<Driver> driverQuery = db.createQuery(
	            "SELECT d FROM Driver d WHERE d.email=:email", Driver.class);
	        driverQuery.setParameter("email", email);
	        
	        boolean driverExists = !driverQuery.getResultList().isEmpty();
	        
	        TypedQuery<Traveler> travelerQuery = db.createQuery(
	            "SELECT t FROM Traveler t WHERE t.email=:email", Traveler.class);
	        travelerQuery.setParameter("email", email);
	        
	        boolean travelerExists = !travelerQuery.getResultList().isEmpty();
	        
	       
	        if (driverExists || travelerExists) {
	            throw new ErabiltzaileaDagoenekoErregistratutaException("Already exists a user with the same email");
	        }
	        
	      
	        
	        db.getTransaction().begin();
	      
	        User newUser;
	        if (isDriver) {
	            newUser = new Driver(email, password, name, surname);
	        } else {
	            newUser = new Traveler(email, password, name, surname);
	        }
	        db.persist(newUser);
	        db.getTransaction().commit();
	        return newUser;
	        
	    } catch (ErabiltzaileaDagoenekoErregistratutaException e) {
	       
	        if (db.getTransaction().isActive()) {
	            db.getTransaction().rollback();
	        }
	        throw e;
	    } catch (Exception e) {
	        if (db.getTransaction().isActive()) {
	            db.getTransaction().rollback();
	        }
	        throw new RuntimeException("Error inesperado durante el registro: " + e.getMessage(), e);
	    }
	}
	
	public User login(String email, String password) {
	    try {
	        if (email == null || password == null) return null;

	        TypedQuery<Driver> driverQuery = db.createQuery(
	            "SELECT d FROM Driver d WHERE d.email=:email AND d.password=:password", Driver.class);
	        driverQuery.setParameter("email", email);
	        driverQuery.setParameter("password", password);

	        try {
	            Driver driver = driverQuery.getSingleResult();
	            return driver;
	        } catch (NoResultException e) {
	            TypedQuery<Traveler> travelerQuery = db.createQuery(
	                "SELECT t FROM Traveler t WHERE t.email=:email AND t.password=:password", Traveler.class);
	            travelerQuery.setParameter("email", email);
	            travelerQuery.setParameter("password", password);

	            try {
	                Traveler traveler = travelerQuery.getSingleResult();
	                return traveler;
	            } catch (NoResultException e2) {
	                return null;
	            }
	        }
	    } catch (Exception e) {
	        return null;
	    }
	}

		public List<Driver> getDrivers() {
		    try {
		        return db.createQuery("SELECT d FROM Driver d", Driver.class)
		                 .getResultList();
		    } catch (Exception e) {
		               System.out.println("Error en getActiveDrivers: " + e.getMessage());
		        e.printStackTrace();
		        return new ArrayList<Driver>();
		    }
		}

	public Driver getDriver(String email) {
		return db.find(Driver.class, email);
	}
	
	public void initializeDB(){
		
		db.getTransaction().begin();

		try {
			
			Long driverCount = db.createQuery("SELECT COUNT(d) FROM Driver d", Long.class)
					.getSingleResult();
			
			if (driverCount > 0) {
				System.out.println("Datu-basea jadanik hasieratuta dago (" + driverCount + " driver)");
				db.getTransaction().rollback();
				return;
			}
			
			System.out.println("Datu-basea hutsik dago, hasieratzen...");

			Calendar today = Calendar.getInstance();
			
			int month=today.get(Calendar.MONTH);
			int year=today.get(Calendar.YEAR);
			if (month==12) { month=1; year+=1;}  
			
			//Create drivers 
			Driver driver1=new Driver("driver198@gmail.com","abc123", "Ane", "Gaztañaga");
			Driver driver2 = new Driver("driver1982332@gmail.com","1dfasd","Ane","Ane");
			Driver driver3 = new Driver("driver3343@gmail.com", "pass", "Test driver", "test");
			
			//Create traveler
			Traveler traveler1 = new Traveler("traveler198324233@gmail.com","2adsfds","Patxi","Arruabarrena");
			Traveler traveler2 = new Traveler("traveler198@gmail.com","3gfsdd324","Izel","Goikoetxea");
			
			Date data = new Date();
			Calendar cal = Calendar.getInstance();
			cal.set(2025, 5, 20);
			data = cal.getTime();
			Ride r = driver2.addRide("Donostia", "Bilbo", UtilDate.trim(data), 5.00, 5);
						
			Ride r2 = driver2.addRide("Usurbil", "Donosti", UtilDate.trim(data), 3.00, 5);
			
			db.persist(driver1);
			db.persist(driver2);
			db.persist(driver3);
			db.persist(traveler1);
			db.persist(traveler2);
			db.getTransaction().commit();
			
			System.out.println("Db initialized");
		}
		catch (Exception e){
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			System.err.println("Errorea datu-basea hasieratzerakoan: " + e.getMessage());
			e.printStackTrace();
		}
	}

public List<Ride> getRidesByDriverEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
        return new ArrayList<>();
    }
    try {
        return db.createQuery(
                "SELECT r FROM Ride r JOIN r.driver d WHERE d.email = :email ORDER BY r.date DESC",
                Ride.class)
                .setParameter("email", email)
                .getResultList();
    } catch (Exception e) {
        System.out.println("Error en getRidesByDriverEmail: " + e.getMessage());
        e.printStackTrace();
               return new ArrayList<>();
    }
}
	
}