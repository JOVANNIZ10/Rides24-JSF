package eredua.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import businessLogic.BLFacade;
import domain.Ride;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("queryRideBean")
@SessionScoped
public class QueryRideBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String arrivalCity;
	private String departCity;
	private Date date;
	private List<Ride> rides;

	public void queryRides() {
	    if (departCity == null || departCity.trim().isEmpty() ||
	        arrivalCity == null || arrivalCity.trim().isEmpty() ||
	        date == null) {

	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR,
	            "Todos los campos son obligatorios", null));
	        return; 
	    }

	    BLFacade facadeBL = FacadeBean.getBusinessLogic();
	    rides = facadeBL.getRides(departCity, arrivalCity, date);

	    if (rides == null || rides.isEmpty()) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_WARN,
	            "No hay viajes con los parámetros introducidos", null));
	    }
	}

	public QueryRideBean() {
		
	}
	public String getArrivalCity() {
		return arrivalCity;
	}
	public void setArrivalCity(String arrivalCity) {
		this.arrivalCity = arrivalCity;
	}
	public String getDepartCity() {
		return departCity;
	}
	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Ride> getRides() {
		return rides;
	}

	public void setRides(List<Ride> rides) {
		this.rides = rides;
	}
	
	
	
}
