package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.ClientsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;

public class ClientsManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ClientsManagementController
	private ClientsManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Client> oListClients;

	/**
	 * Manipulation de la fenêtre
	 * 
	 * @param _containingStage IN : Fenêtre physique ou est la scène contenant le
	 *                         fichier xml contrôlé par this
	 * @param _cm              IN : Contrôleur de Dialogue associé à
	 *                         ClientsManagementController
	 * @param _dbstate         IN : Etat courant de l'application
	 */
	public void initContext(Stage _containingStage, ClientsManagement _cm, DailyBankState _dbstate) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	/**
	 * Validation de l'état des composants
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListClients = FXCollections.observableArrayList();
		this.lvClients.setItems(this.oListClients);
		this.lvClients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvClients.getFocusModel().focus(-1);
		this.lvClients.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

	/**
	 * Validation de l'état des composants
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 * Validation de l'état des composants
	 * 
	 * @param e IN : Event
	 * @return Object : null
	 */
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Client> lvClients;
	@FXML
	private Button btnDesactClient;
	@FXML
	private Button btnModifClient;
	@FXML
	private Button btnComptesClient;

	/**
	 * Validation de l'état des composants
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Action du bouton Rechercher
	 */
	@FXML
	private void doRechercher() {
		int numCompte;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					this.txtNum.setText("");
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			numCompte = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (numCompte != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}

		// Recherche des clients en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les clients
		ArrayList<Client> listeCli;
		listeCli = this.cmDialogController.getlisteComptes(numCompte, debutNom, debutPrenom);

		this.oListClients.clear();
		this.oListClients.addAll(listeCli);
		this.validateComponentState();
	}

	/**
	 * Méthode appelée lors de l'appui sur le bouton Comptes Client
	 * 
	 */
	@FXML
	private void doComptesClient() {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client client = this.oListClients.get(selectedIndice);
			this.cmDialogController.gererComptesClient(client);
		}
	}

	/**
	 * Méthode appelée lors de l'appui sur le bouton Modifier Client
	 * 
	 */
	@FXML
	private void doModifierClient() {

		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client cliMod = this.oListClients.get(selectedIndice);
			Client result = this.cmDialogController.modifierClient(cliMod);
			if (result != null) {
				this.oListClients.set(selectedIndice, result);
			}
		}
	}

	/**
	 * Méthode appelée lors de l'appui sur le bouton Désactiver Client
	 * 
	 */
	@FXML
	private void doDesactiverClient() {
	}

	/**
	 * Méthode appelée lors de l'appui sur le bouton Nouveau Client
	 */
	@FXML
	private void doNouveauClient() {
		Client client;
		client = this.cmDialogController.nouveauClient();
		if (client != null) {
			this.oListClients.add(client);
		}
	}

	/**
	 * Validation de l'état des composants
	 * 
	 * @author Bastien RECORD
	 */
	private void validateComponentState() {
		// Non implémenté => désactivé
		this.btnDesactClient.setDisable(true);
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifClient.setDisable(false);
			this.btnComptesClient.setDisable(false);
		} else {
			this.btnModifClient.setDisable(true);
			this.btnComptesClient.setDisable(true);
		}
	}
}
