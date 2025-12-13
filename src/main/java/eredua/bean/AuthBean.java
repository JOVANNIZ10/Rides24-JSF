package eredua.bean; 

import businessLogic.BLFacade; 
import domain.User;
import exceptions.ErabiltzaileaDagoenekoErregistratutaException;

import java.io.Serializable;
import jakarta.enterprise.context.SessionScoped; // Ámbito de CDI (Jakarta)
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject; // Anotación de inyección de CDI (Jakarta)
import jakarta.inject.Named; // Anotación para Managed Bean de CDI (Jakarta)

@Named("authBean") // Ahora es un Managed Bean de CDI
@SessionScoped 
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // Inyección de la lógica de negocio (asumiendo que BLFacade es un CDI Bean) 
    
    // --- Propiedades Comunes (Bindings de formularios) ---
    private String email;
    private String password;
    
    // --- Propiedades Específicas del Registro ---
    private String nombre;
    private String apellido;
    private boolean isDriver;

    // --- Propiedades de Sesión/Estado ---
    private User loggedUser; 
    
    // ===================================================================
    // 1. LÓGICA DE LOGIN
    // ===================================================================

 // Dentro de AuthBean.java

    public String login() {
        // Validación simple, el required="true" de la vista ya ayuda mucho
        if (email == null || password == null) return null;

        // 1. Obtener la lógica de negocio
        BLFacade facadeBL = FacadeBean.getBusinessLogic();
        if (facadeBL == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error de Sistema", "BLFacade no inicializado."));
            return null;
        }

        // 2. Intentar loguear
        loggedUser = facadeBL.login(email, password); 

        if (loggedUser != null) {
            // Éxito: Limpiar solo la contraseña
            this.password = null; 
            
            FacesContext.getCurrentInstance().addMessage(
                null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Bienvenido, " + loggedUser.getName())
            );

            // 3. LÓGICA DE BIFURCACIÓN DE REDIRECCIÓN
            if (loggedUser instanceof domain.Driver) {
                // Si el usuario es un Driver (Conductor)
                return "LandingPageDR?faces-redirect=true"; 
            } else {
                // Si el usuario es un Traveler (Viajero) u otro tipo de usuario por defecto
                return "LandingPageTR?faces-redirect=true";
            }
        } else {
            // Falla: Mostrar mensaje global
            FacesContext.getCurrentInstance().addMessage(
                null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Login", "Email o contraseña incorrectos.")
            );
            return null; 
        }
    }
    
    // ===================================================================
    // 2. LÓGICA DE REGISTRO
    // ===================================================================

    public String register() {
        // Validación de campos del registro (solo si h:message no captura un error)
        if (nombre == null || apellido == null || email == null || password == null) return null;
        
        try {
        	BLFacade facadeBL = FacadeBean.getBusinessLogic();
            // Utilizamos el objeto blFacade inyectado
            User newUser = facadeBL.register(email, nombre, apellido, password, isDriver);
            
            // Si llega aquí sin excepción, el registro fue exitoso
            resetRegistrationFields();
            FacesContext.getCurrentInstance().addMessage(
                null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro Exitoso", "Tu cuenta ha sido creada. ¡Inicia Sesión!")
            );
            return "Login?faces-redirect=true"; 
            
        } catch (ErabiltzaileaDagoenekoErregistratutaException e) {
            // Error de email duplicado
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
    
    // ===================================================================
    // 3. MÉTODOS DE UTILIDAD Y SESIÓN
    // ===================================================

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

    // --- GETTERS Y SETTERS ---
    // (Omitidos aquí por brevedad, pero deben estar presentes para todas las propiedades)
    
    // Implementación de Getters y Setters:
    
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