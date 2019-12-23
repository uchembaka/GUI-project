
//standard javafx imports

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

//imports for application components
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
//imports for layout
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

//import for files
import java.io.File;

import java.net.URL;

//Media imports
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.Media;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.util.Duration;

/**
 * @author Uche Mbaka
 *
 */
public class BlastBox extends Application {
	// declare components at class level
	// Label
	Label lblAvailableTrack, lblSelectedTrack, lblVolume, lblStatus, lblPlayStatus;

	// ListViews
	ListView<String> lvAvailableTrack, lvSelectedTrack;

	// Buttons
	Button btnAdd, btnRemove, btnRemoveAll, btnPlay, btnPause, btnStop;

	// Slider
	Slider slVolume, slPlayStatus;

	// MediaPlayer and media
	MediaPlayer mpPlay;

	Media media;

	/*
	 * Stat is used to track current status of player. 
	 * The initial stat 0 indicates the player was either stopped or no song has been played
	 * Stat = 1 indicates the current song was paused and clicking on the play button resumes the song
	 * A better class to use to check status of player is the javafx MediaPlayer.Status. But due to limited
	 * time we were unable to use it, because the entire playSong method and event on action for play/pause
	 * and stop had to change.
	 */
	int stat = 0;

	//Duration
	Duration duration;

	public BlastBox() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// instantiate components

		// Labels
		lblAvailableTrack = new Label("Available Track");
		lblSelectedTrack = new Label("Selected Tracks");
		lblVolume = new Label("Volume");
		lblStatus = new Label("Status: ");

		// Buttons
		btnAdd = new Button("Add >");
		btnRemove = new Button("< Remove");
		btnRemoveAll = new Button("<< Remove All");
		//Button Play used as paused too
		//ImageView to create a button with an icon
		btnPlay = new Button("Play", new ImageView("./play.png"));
		// btnPause = new Button("Pause");
		btnStop = new Button("Stop", new ImageView("./stop.png"));

		// set button max width and style
		btnAdd.setMaxWidth(110);
		btnRemove.setMaxWidth(110);
		btnRemoveAll.setMaxWidth(110);
		btnPlay.setMaxWidth(110);
		// btnPause.setMaxWidth(100);
		btnStop.setMaxWidth(110);

		// set btnAdd on action
		btnAdd.setOnAction(ae -> {
			if (lvAvailableTrack.getSelectionModel().getSelectedItem() != null) {
				lvSelectedTrack.getItems().add(lvAvailableTrack.getSelectionModel().getSelectedItem());
				lvAvailableTrack.getItems().remove(lvAvailableTrack.getSelectionModel().getSelectedIndex());

			}
		});

		// set action btnRemove
		btnRemove.setOnAction(ae -> {
			if (lvSelectedTrack.getSelectionModel().getSelectedItem() != null) {
				lvAvailableTrack.getItems().add(lvSelectedTrack.getSelectionModel().getSelectedItem());
				lvSelectedTrack.getItems().remove(lvSelectedTrack.getSelectionModel().getSelectedIndex());
			}
		});

		// set action btnRemovell
		btnRemoveAll.setOnAction(ae -> {
			if (!(lvSelectedTrack.getItems().isEmpty())) {
				lvAvailableTrack.getItems().addAll(lvSelectedTrack.getItems());
				lvSelectedTrack.getItems().removeAll(lvSelectedTrack.getItems());
			}
		});
		

		// set on action play
		btnPlay.setOnAction(ae -> {
			if (lvSelectedTrack.getSelectionModel().getSelectedItem() != null) {
				// call play music method on selected track

				if (btnPlay.getText() == "Pause") {
					// mpPlay.play();
					mpPlay.pause();
					btnPlay.setText("Play");
					btnPlay.setGraphic(new ImageView("./play.png"));
					stat = 1;
				} else if (btnPlay.getText() == "Play") {
					if (stat == 1) {// stat = 1 means the current music was paused
						mpPlay.play();
						btnPlay.setText("Pause");
						btnPlay.setGraphic(new ImageView("./pause.png"));
					} else {
						playMusic("./songs/" + lvSelectedTrack.getSelectionModel().getSelectedItem().toString());
						btnPlay.setText("Pause");
						btnPlay.setGraphic(new ImageView("./pause.png"));
					}

				}
			} else if (btnPlay.getText() == "Pause" && (lvSelectedTrack.getItems().isEmpty())) {
				/*
				 * this else if block makes sure that play/pause button still works even if an
				 * item is not currently selected but a song is already playing and disable both stop and play/pause button since selected track list is now empty.
				 */
				mpPlay.pause();
				btnPlay.setText("Play");
				btnPlay.setGraphic(new ImageView("./play.png"));

			}
		});

		// set on action stop
		btnStop.setOnAction(ae -> {
			if (lvSelectedTrack.getSelectionModel().getSelectedItem() != null) {
				/*
				 * The right side of the OR in the if statement is used to makes sure that stop button still works even if an
				 * item is not currently selected but a song is already playing.
				 * And disable both stop and play/pause button since selected track list is now empty.
				 */
				mpPlay.stop();
				stat = 0;
				btnPlay.setText("Play");
				btnPlay.setGraphic(new ImageView("./play.png"));
			} else if(btnPlay.getText() == "Pause" && (lvSelectedTrack.getItems().isEmpty())) {
				mpPlay.stop();
				btnPlay.setText("Play");
				btnPlay.setGraphic(new ImageView("./play.png"));

			}

		});

		// ListViews
		lvAvailableTrack = new ListView<String>();
		lvSelectedTrack = new ListView<String>();

		// Slider
		slVolume = new Slider();
		slPlayStatus = new Slider();

	}// init()

	private void playMusic(String trackToPlay) {

		final URL resource = getClass().getResource(trackToPlay);
		// instantiate media with name of track to play
		media = new Media(resource.toString());
		mpPlay = new MediaPlayer(media);
		mpPlay.play();

		slVolume.setValue(mpPlay.getVolume() * 100); // max volume
		
		/*
		 * the volume of the player is tracked by adding a listener and 
		 * the current value invalidated each time the user move the slide
		 * and the current value is set at play volume.
		 */
		slVolume.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(javafx.beans.Observable observable) {
				mpPlay.setVolume(slVolume.getValue() / 100);
			}
		});

		
		/*
		 * Play status slider/time
		 * The slider maximum length is set to the length of track in seconds.
		 * the slider pointer is set to the newTime value observed by the listener.
		 */
		mpPlay.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
			// set max length of status slider to length of track
			slPlayStatus.setMax(mpPlay.getTotalDuration().toSeconds());

			if (!slPlayStatus.isValueChanging()) {
				// slider
				slPlayStatus.setValue(newTime.toSeconds());
				// timer
				lblStatus.setText(String.format("Status: " + "%4d:%02d:%04.1f", (int) newTime.toHours(),
						(int) (newTime.toMinutes() % 60), (newTime.toSeconds() % 60)));
			}
		});

		// seek when pointer on slider is dragged and start reading from that point
		slPlayStatus.setOnMouseClicked(ae -> {
			mpPlay.seek(Duration.seconds(slPlayStatus.getValue()));
		});

	}

	void readMp3(String dirName) {
		// read songs folder
		File file = new File(dirName);
		File[] files = file.listFiles();
		for (File f : files) {
			lvAvailableTrack.getItems().add(f.getName());
		}

	}// readMp3()

	@Override
	public void start(Stage pStage) throws Exception {
		// Set stage title
		pStage.setTitle("MP3 Blast Box v1.0.0");

		// Set stage width and height
		pStage.setHeight(500);
		pStage.setWidth(630);

		// create layout
		GridPane gp = new GridPane();

		// Gridpane spacing and gaps
		gp.setVgap(20);
		gp.setHgap(20);
		gp.setPadding(new Insets(25));
		gp.setAlignment(Pos.CENTER);

		// Add components to GridPane
		gp.add(lblAvailableTrack, 0, 0);
		gp.add(lblSelectedTrack, 2, 0);

		gp.add(lvAvailableTrack, 0, 1);
		gp.add(lvSelectedTrack, 2, 1);

		gp.add(lblStatus, 0, 3);

		gp.add(slPlayStatus, 0, 4, 3, 1);

		// Add button to HBox
		VBox vbBtns = new VBox();
		vbBtns.getChildren().addAll(btnAdd, btnRemove, btnRemoveAll, btnPlay, btnStop, lblVolume, slVolume);

		btnRemove.setDisable(true);
		btnRemoveAll.setDisable(true);

		/*
		 * disableProperty() is used to set buttons to disabled. The button is set to disabled only when the binding condition is true. 
		 * The button will become disable when the statement inside the unidirectional bind is true.
		 */
		btnAdd.disableProperty().bind(lvAvailableTrack.getSelectionModel().selectedItemProperty().isNull());
		btnRemove.disableProperty().bind(lvSelectedTrack.getSelectionModel().selectedItemProperty().isNull());
		btnRemoveAll.disableProperty().bind(Bindings.isEmpty(lvSelectedTrack.getItems()));

		vbBtns.setSpacing(10);

		gp.add(vbBtns, 1, 1);

		// create scene
		Scene s = new Scene(gp);

		// Apply s style to the scene using a style sheet
		s.getStylesheets().add("style_player.css");

		// set scene
		pStage.setScene(s);

		// populate listview
		readMp3("./songs");

		// Show stage
		pStage.show();

	}// start()

	@Override
	public void stop() {

	}// stop()

	public static void main(String[] args) {
		launch();

	}// main()

}// class
