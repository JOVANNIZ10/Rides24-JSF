package eredua.bean; 

import businessLogic.BLFacade; 
import domain.User;
import exceptions.ErabiltzaileaDagoenekoErregistratutaException;

import java.io.Serializable;
import jakarta.enterprise.context.SessionScoped; 
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject; 
import jakarta.inject.Named; 

@Named("authBean") 
@SessionScoped 
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
  
    private String email;
    private String password;
    
   
    private String nombre;
    private String apellido;
    private boolean isDriver;

   
    private User loggedUser; 
    
   

    public String login() {
     
        if (email == null || password == null) return null;

 
        BLFacade facadeBL = FacadeBean.getBusinessLogic();
        if (facadeBL == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error de Sistema", "BLFacade no inicializado."));
            return null;
        }

      
        loggedUser = facadeBL.login(email, password); 

        if (loggedUser != null) {
            
            this.password = null; 
            
            FacesContext.getCurrentInstance().addMessage(
                null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Bienvenido, " + loggedUser.getName())
            );

            
            if (loggedUser instanceof domain.Driver) {
                
                return "LandingPageDR?faces-redirect=true"; 
            } else {
               
                return "LandingPageTR?faces-redirect=true";
            }
        } else {
            
            FacesContext.getCurrentInstance().addMessage(
                null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Login", "Email o contraseña incorrectos.")
            );
            return null; 
        }
    }
    
  

    public String register() {
       
        if (nombre == null || apellido == null || email == null || password == null) return null;
        
        try {
        	BLFacade facadeBL = FacadeBean.getBusinessLogic();
            // Utilizamos el objeto blFacade inyectado
            User newUser = facadeBL.register(email, nombre, apellido, password, isDriver);
            
           
            resetRegistrationFields();
            FacesContext.getCurrentInstance().addMessage(
                null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro Exitoso", "Tu cuenta ha sido creada. ¡Inicia Sesión!")
            );
            return "Login?faces-redirect=true"; 
            
        } catch (ErabiltzaileaDagoenekoErregistratutaException e) {
            
            FacesContext.getCurrentInstance().addMessage(
                null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Registro", "Ya existe un usuario con ese email.")
            );
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(
                null, 
                new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error Grave", "No se pudo registrar la cuenta. Intente más tarde.")
            );
            return null;
        }
    }
    
 

    private void resetRegistrationFields() {
        this.nombre = null;
        this.apellido = null;
        this.email = null;
        this.password = null;
        this.isDriver = false;
    }

    public String logout() {
        this.loggedUser = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "LandingPageUR?faces-redirect=true"; 
    }

  
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public boolean isIsDriver() { return isDriver; }
    public void setIsDriver(boolean isDriver) { this.isDriver = isDriver; }

    public User getLoggedUser() { return loggedUser; }
    public boolean isLoggedIn() { return this.loggedUser != null; }
    public String getUsername() {
        return (loggedUser != null) ? loggedUser.getName() : "";
    }
    
}