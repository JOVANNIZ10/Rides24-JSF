package eredua.bean;

import java.io.Serializable;
import java.util.List;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Ride;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("gidariBean")
@ApplicationScoped
public class DriverBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
    private List<Driver> drivers;
    private List<Ride> rides;

    @PostConstruct
    public void init() {
    	BLFacade facadeBL = FacadeBean.getBusinessLogic();
        drivers = facadeBL.getDrivers();
    }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public List<Driver> getDrivers() { return drivers; }



    public String erakutsiBidaiak() {
           if (email == null) {
               rides = List.of();
               return null; 
           }

           Driver seleccionado = drivers.stream()
                   .filter(d -> email.equals(d.getEmail()))
                   .findFirst()
                   .orElse(null);

           if (seleccionado == null || seleccionado.getEmail() == null) {
               rides = List.of();
               return null;
           }
           BLFacade facadeBL = FacadeBean.getBusinessLogic();
          
           rides = facadeBL.getRidesByDriverEmail(seleccionado.getEmail());

           return null;
       }
    public List<Ride> getRides() { return rides;}

}
