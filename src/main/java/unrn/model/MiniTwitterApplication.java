

package unrn.model;

import jakarta.persistence.EntityManagerFactory;
import unrn.service.TwitterService;
import unrn.util.EmfBuilder;

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



