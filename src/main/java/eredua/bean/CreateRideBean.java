package eredua.bean;

import java.util.Date;
import java.util.List;

import org.primefaces.event.SelectEvent;

import businessLogic.BLFacade;
import domain.Ride;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("createRide")
@ApplicationScoped
public class CreateRideBean {

	@Inject
	private AuthBean rides;

	private String from;
	private String to;
	private Date data;
	private int seats;
	private float price;
	private String email;

	private List<String> gertaerak;

	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getGertaerak() {
		if (FacadeBean.getBusinessLogic() != null) {
			gertaerak = FacadeBean.getBusinessLogic().getDepartCities();
		}
		return gertaerak;
	}
	

	
	public String createRideAction() {
	    try {
	       
	        email = rides.getEmail(); 
	        
	        Ride ride = FacadeBean.getBusinessLogic().createRide(from, to, data, seats, price, email);

	        if (ride != null) {
	          
	            from = "";
	            to = "";
	            data = null; 
	            seats = 0;
	            price = 0;

	            FacesContext.getCurrentInstance().addMessage(null,
	                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Bidaia sortuta", "Bidaia sortu da!"));
	            
	            return null; 
	        } else {
	            FacesContext.getCurrentInstance().addMessage(null,
	                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea", "Ezin izan da bidaia sortu"));
	        }

	    } catch (RideMustBeLaterThanTodayException e) {
	        FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea", "Data gaur baino berandugo izan behar da!"));
	    } catch (RideAlreadyExistException e) {
	        FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea", "Bidaia lehenagotik existitzen da!"));
	    }

	    return null; 
	}
}

