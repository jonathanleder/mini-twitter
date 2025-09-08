

package unrn.model;

import util.EmfBuilder;
import jakarta.persistence.EntityManagerFactory;

public class MiniTwitterApplication {

	public static void main(String[] args) {
	EntityManagerFactory emf = new EmfBuilder()
		.clientAndServer()
		.withDropAndCreateDDL()
		.build();

	TwitterService service = new TwitterService(emf);
	service.crearUsuario("usuario123");
	service.crearUsuario("usuario456");
	emf.close();
	}
}



