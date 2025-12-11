package eredua.bean;

import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import dataAccess.DataAccess;

public class FacadeBean {
	private static FacadeBean singleton = new FacadeBean();
	private static BLFacade facadeInterface;

	private FacadeBean() {
		try {
			System.out.println("=== FacadeBean hasieratzen ===");
			
			// Datu-basea hasieratu
			DataAccess dataAccess = new DataAccess();
			dataAccess.open();
			dataAccess.initializeDB();
			dataAccess.close();
			
			System.out.println("Datu-basea hasieratuta!");
			
			// BusinessLogic sortu
			facadeInterface = new BLFacadeImplementation(new DataAccess());
			
		} catch (Exception e) {
			System.out.println("FacadeBean: negozioaren logika sortzean errorea: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static BLFacade getBusinessLogic() {
		return facadeInterface;
	}
}
