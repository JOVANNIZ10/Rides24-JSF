package eredua.bean;

import java.io.Serializable;
import java.util.Date;

import businessLogic.BLFacade;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("createRide")
@SessionScoped
public class CreateRideBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String departCity;
	private String arrivalCity;
	private int numberOfSeats;
	private float price;
	private Date date;
	public CreateRideBean(){
		
	}
	public String getDepartCity() {
		return departCity;
	}
	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}
	public String getArrivalCity() {
		return arrivalCity;
	}
	public void setArrivalCity(String arrivalCity) {
		this.arrivalCity = arrivalCity;
	}
	public int getNumberOfSeats() {
		return numberOfSeats;
	}
	public void setNumberOfSeats(int numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String createRideAction() {
	    try {
	        BLFacade facadeBL = FacadeBean.getBusinessLogic();
	        facadeBL.createRide(departCity, arrivalCity, date, numberOfSeats, price, "driver1@gmail.com");
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_INFO, "Arrakasta bidaia sortuta", ""));
	    } catch (RideMustBeLaterThanTodayException e) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
	    } catch (RideAlreadyExistException e) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
	    } catch (Exception e) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_FATAL, e.getMessage(), ""));
	    }
	    return null; 
	}

}

