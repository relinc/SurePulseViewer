package net.relinc.viewer.GUI;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.PopOver;
import net.relinc.libraries.application.LineChartWithMarkers;
import net.relinc.libraries.data.DataFile;
import net.relinc.libraries.data.DataSubset;
import net.relinc.libraries.data.Descriptor;
import net.relinc.libraries.data.ReflectedPulse;
import net.relinc.libraries.data.TransmissionPulse;
import net.relinc.libraries.data.ModifierFolder.LowPass;
import net.relinc.libraries.data.ModifierFolder.Modifier;
import net.relinc.libraries.fxControls.NumberTextField;
import net.relinc.libraries.sample.CompressionSample;
import net.relinc.libraries.sample.HopkinsonBarSample;
import net.relinc.libraries.sample.LoadDisplacementSampleResults;
import net.relinc.libraries.sample.Sample;
import net.relinc.libraries.staticClasses.Converter;
import net.relinc.libraries.staticClasses.Dialogs;
import net.relinc.libraries.staticClasses.SPLogger;
import net.relinc.libraries.staticClasses.SPMath;
import net.relinc.libraries.staticClasses.SPOperations;
import net.relinc.libraries.staticClasses.SPSettings;
import net.relinc.libraries.staticClasses.SPTracker;
import net.relinc.viewer.application.SampleSession;
import net.relinc.viewer.application.Session;
import net.relinc.viewer.application.MetricMultiplier.Unit;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class HomeController extends CommonGUI {
	
//	private static final Logger logger =
//	        Logger.getLogger(HomeController.class.getName());
	//private static final Logger logger = SPOperations.getLogger(HomeController.class.getName());
	
	public List<String> parameters;
	PopOver about;

	List<String> colorString = Arrays.asList("#A0D6C8", "#D18357", "#DEAADE", "#DDD75D", "#819856", "#78ADD4",
			"#A1E17C", "#71E5B3", "#D8849C", "#5BA27E", "#5E969A", "#C29E53", "#8E89A4", "#C6DB93", "#E9A38F",
			"#E3B4C5", "#63D7DF", "#C57370", "#BFC6E4", "#AC7A9C");



	@FXML VBox leftVBox;
	@FXML VBox middleBottomVbox;
	@FXML AnchorPane chartAnchorPane;
	@FXML VBox vboxForDisplayedChartsListView;
	@FXML VBox vBoxHoldingCharts;
	@FXML HBox globalLoadDataFilterHBox; //contains the numeric box
	@FXML VBox globalLoadDataFilterVBox;
	@FXML VBox globalDisplacementDataVBox;
	@FXML HBox globalDisplacementDataHBox;
	@FXML VBox dataModifiersVBox;

	@FXML RadioButton engineeringRadioButton;
	@FXML RadioButton trueRadioButton;
	@FXML RadioButton metricRadioButton;
	@FXML RadioButton englishRadioButton;
	@FXML RadioButton secondsRadioButton;
	@FXML RadioButton milliSecondsRadioButton;
	@FXML RadioButton microSecondsRadioButton;
	@FXML RadioButton nanoSecondsRadioButton;
	@FXML RadioButton picoSecondsRadioButton;
	@FXML CheckBox loadDisplacementCB;
	@FXML public CheckBox zoomToROICB;
	@FXML SplitPane homeSplitPane;
	@FXML Button buttonOpenExportMenu;

	@FXML TextField tbAvgYValue;
	@FXML TextField tbSlopeValue;
	@FXML TextField tbKValue;
	@FXML TextField tbAvgIntegralValue;
	@FXML Label averageYValueLabel;
	@FXML RadioButton radioSetBegin;
	@FXML RadioButton radioSetEnd;
	@FXML public ChoiceBox<String> choiceBoxRoi;
	@FXML public ChoiceBox<Sample> roiSelectionModeChoiceBox;
	@FXML Accordion leftAccordion;
	@FXML public CheckBox holdROIAnnotationsCB;
	@FXML Button showSampleDirectoryButton;
	@FXML TextField maxYValueTF;
	@FXML TextField durationTF;
	@FXML TextField averageKValueTF;
	@FXML TextField averageNValueTF;
	@FXML Label averageMaxValueLabel;
	@FXML Label xValueLabel;
	@FXML Label yValueLabel;
	@FXML TitledPane regionOfInterestTitledPane;
	@FXML TitledPane chartingTitledPane;
	@FXML TitledPane dataModifiersTitledPane;
	
	//temp trim
	@FXML RadioButton tempTrimBeginRadioButton;
	@FXML RadioButton tempTrimEndRadioButton;


	boolean mouseHoveringOverSample = false;
	boolean exportMenuOpen;

	ToggleGroup englishMetricGroup = new ToggleGroup();
	ToggleGroup engineeringTrueGroup = new ToggleGroup();
	ToggleGroup exportEnglishMetricGroup = new ToggleGroup();
	ToggleGroup exportEngineeringTrueGroup = new ToggleGroup();
	ToggleGroup roiToggleGroup = new ToggleGroup();
	ToggleGroup timeScaleToggleGroup = new ToggleGroup();
	ToggleGroup tempTrimBeginEndToggleGroup = new ToggleGroup();

	CheckListView<String> displayedChartListView = new CheckListView<String>();


	double tintForceAmount = 50; //the amount to tint the back face force series line
	
	boolean showROIOnChart = false;
	double widthOfLeftPanel;
	
	//global filter
	NumberTextField globalLoadDataFilterTextField = new NumberTextField("KHz", "KHz");
	NumberTextField globalDisplacementFilterTextField = new NumberTextField("KHz", "KHz");

	ExportGUI rightOptionPane = new ExportGUI(this);;
	SampleDirectoryGUI sampleDirectoryGUI = new SampleDirectoryGUI(this);
	VideoCorrelationGUI videoCorrelationGUI = new VideoCorrelationGUI(this);
	ChartsGUI chartsGUI = new ChartsGUI(this);
	
	private boolean renderBlock = false;
	
	public void initialize(){
		SPLogger.logger.info("HomeController is initializing");
		
		// Attaching the radio button values to the parent CommonGUI class.
		isEnglish.bindBidirectional(englishRadioButton.selectedProperty());
		isEngineering.bindBidirectional(engineeringRadioButton.selectedProperty());
		isLoadDisplacement.bindBidirectional(loadDisplacementCB.selectedProperty());
		
		showSampleDirectoryButton.setGraphic(SPOperations.getIcon(SPOperations.folderImageLocation));
		
		fillColorList();
		englishRadioButton.setToggleGroup(englishMetricGroup);
		metricRadioButton.setToggleGroup(englishMetricGroup);
		engineeringRadioButton.setToggleGroup(engineeringTrueGroup);
		trueRadioButton.setToggleGroup(engineeringTrueGroup);
		
		metricRadioButton.selectedProperty().bindBidirectional(SPSettings.metricMode); //english button will be taken care of by group.

		radioSetBegin.setToggleGroup(roiToggleGroup);
		radioSetEnd.setToggleGroup(roiToggleGroup);

		secondsRadioButton.setToggleGroup(timeScaleToggleGroup);
		milliSecondsRadioButton.setToggleGroup(timeScaleToggleGroup);
		microSecondsRadioButton.setToggleGroup(timeScaleToggleGroup);
		nanoSecondsRadioButton.setToggleGroup(timeScaleToggleGroup);
		picoSecondsRadioButton.setToggleGroup(timeScaleToggleGroup);

		tempTrimBeginRadioButton.setToggleGroup(tempTrimBeginEndToggleGroup);
		tempTrimEndRadioButton.setToggleGroup(tempTrimBeginEndToggleGroup);

		leftVBox.getChildren().add(1, realCurrentSamplesListView);
		vboxForDisplayedChartsListView.getChildren().add(0,displayedChartListView);
		
		
		globalLoadDataFilterVBox.setStyle("-fx-border-color: #bdbdbd;\n"
                + "-fx-border-insets: -5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: solid;\n");
		globalDisplacementDataVBox.setStyle("-fx-border-color: #bdbdbd;\n"
                + "-fx-border-insets: -5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: solid;\n");

		GridPane grid = new GridPane();
		grid.add(globalLoadDataFilterTextField, 0, 0);
		grid.add(globalLoadDataFilterTextField.unitLabel, 0, 0);
		globalLoadDataFilterHBox.getChildren().add(1,grid);
		globalLoadDataFilterTextField.updateLabelPosition();
		globalLoadDataFilterTextField.setPrefWidth(80);
		
		GridPane grid2 = new GridPane();
		grid2.add(globalDisplacementFilterTextField, 0, 0);
		grid2.add(globalDisplacementFilterTextField.unitLabel, 0, 0);
		globalDisplacementDataHBox.getChildren().add(1, grid2);
		globalDisplacementFilterTextField.updateLabelPosition();
		globalDisplacementFilterTextField.setPrefWidth(80);
		
		dataModifiersVBox.setSpacing(20);
		

		setROITimeValuesToMaxRange();
		renderCharts();

		choiceBoxRoi.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				renderROIResults();
			}

		});

		roiSelectionModeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Sample>() {
			@Override
			public void changed(ObservableValue<? extends Sample> observable, Sample oldValue, Sample newValue) {
				renderROIChoiceBox(); //new command
				renderCharts();
				Sample s = roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem();
				if(s == null || s.placeHolderSample){
					roiSelectionModeChoiceBox.setStyle("");
				}
				else{
					roiSelectionModeChoiceBox.setStyle("-fx-background-color:" +  SPOperations.toHexString(getSampleChartColor(s)));
				}
			}
		});

		
		realCurrentSamplesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		addChartTypeListeners();

		realCurrentSamplesListView.setCellFactory(new Callback<ListView<Sample>, ListCell<Sample>>() {
			@Override
			public CheckBoxListCell<Sample> call(ListView<Sample> listView) {
				final CheckBoxListCell<Sample> listCell = new CheckBoxListCell<Sample>()
				{
					@Override
					public void updateItem(Sample item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
						} else {
							setText(item.getName());
						}
					}
				};
				listCell.setSelectedStateCallback(new Callback<Sample, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(Sample param) {
						return param.selectedProperty();
					}
				});

				listCell.setOnMouseEntered(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if (listCell.getItem() == null)
							return;
						if (about != null)
							about.hide();

						Sample sam = listCell.getItem();
						about = new PopOver();

						VBox vbox = new VBox();
						vbox.getStyleClass().add("aboutVBox");
						Label header = new Label(sam.getName());
						header.setFont(new Font(20));
						header.getStyleClass().add("header");
						Label type = new Label(sam.getSampleType());

						String descriptors = sam.getParametersForPopover(metricRadioButton.isSelected());
						
						Label length = new Label(descriptors);
						VBox dataSubsetControlsVbox = new VBox();
						dataSubsetControlsVbox.setSpacing(5);
						//Label data = new Label(dataDescription);
						ChoiceBox<DataFile> dataFilesChoiceBox = new ChoiceBox<DataFile>();
						ChoiceBox<DataSubset> dataSubssetsChoiceBox = new ChoiceBox<DataSubset>();
						dataFilesChoiceBox.setItems(FXCollections.observableArrayList(sam.DataFiles));
						dataFilesChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataFile>() {
							@Override
							public void changed(ObservableValue<? extends DataFile> observable, DataFile oldValue,
									DataFile newValue) {
								dataSubssetsChoiceBox.setItems(FXCollections.observableArrayList(dataFilesChoiceBox.getSelectionModel().getSelectedItem().dataSubsets));
								dataSubssetsChoiceBox.getSelectionModel().select(0);
							}
						});
						dataSubssetsChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataSubset>() {
							@Override
							public void changed(ObservableValue<? extends DataSubset> observable, DataSubset oldValue,
									DataSubset newValue) {
								dataSubsetControlsVbox.getChildren().clear();
								if(dataSubssetsChoiceBox.getSelectionModel().getSelectedItem() == null)
									return;
								for(Modifier mod : dataSubssetsChoiceBox.getSelectionModel().getSelectedItem().modifiers){
									dataSubsetControlsVbox.getChildren().addAll(mod.getViewerControls());
								}
							}
						});
						dataFilesChoiceBox.getSelectionModel().select(0);
						header.setTextAlignment(TextAlignment.CENTER);
						type.setTextAlignment(TextAlignment.LEFT);

						TableView<Descriptor> dictionaryTableView = new TableView<Descriptor>();

						dictionaryTableView.getColumns().clear();
						dictionaryTableView.setEditable(false);
						dictionaryTableView.setPrefHeight(300);

						TableColumn<Descriptor, String> key = new TableColumn<Descriptor, String>("Parameter");
						TableColumn<Descriptor, String> value = new TableColumn<Descriptor, String>("Value");

						key.setCellValueFactory(new PropertyValueFactory<Descriptor, String>("key"));
						key.setCellFactory(TextFieldTableCell.forTableColumn());

						value.setCellValueFactory(new PropertyValueFactory<Descriptor, String>("value"));
						value.setCellFactory(TextFieldTableCell.forTableColumn());


						value.setPrefWidth(200);
						key.setMinWidth(200);

						dictionaryTableView.getColumns().add(key);
						dictionaryTableView.getColumns().add(value);

						dictionaryTableView.setItems(sam.descriptorDictionary.descriptors);
						dictionaryTableView.setPrefHeight(0);

						Label numberOfReflectionsLabel = new Label("Number of Reflections: " + SPOperations.round(sam.results.getNumberOfReflections(), 1));
						vbox.getChildren().addAll(header, type, numberOfReflectionsLabel, length, dictionaryTableView);
						vbox.setAlignment(Pos.TOP_LEFT);
						vbox.setSpacing(5);
						vbox.setPrefHeight(400);
						vbox.setPadding(new Insets(10));
						VBox.setVgrow(dictionaryTableView, Priority.ALWAYS);
						AnchorPane.setBottomAnchor(vbox, 0.0);
						AnchorPane.setLeftAnchor(vbox, 0.0);
						AnchorPane.setTopAnchor(vbox, 0.0);
						AnchorPane.setRightAnchor(vbox, 0.0);
						about.setContentNode(vbox);
						about.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
						about.setCornerRadius(2);
						about.setArrowSize(6);
						about.setAutoHide(true);
						about.getContentNode().setOnMouseExited(new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								if(about != null){
									javafx.scene.shape.Rectangle rec = new javafx.scene.shape.Rectangle(about.getX(), about.getY(), about.getWidth(), about.getHeight());
									if(!rec.contains(new Point2D(event.getScreenX(), event.getScreenY())))
										about.hide();
								}

							}
						});

						about.show(listCell);
					}
				});
				listCell.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if(about != null)
						{
							javafx.scene.shape.Rectangle rec = new javafx.scene.shape.Rectangle(about.getX(), about.getY(), about.getWidth(), about.getHeight());
							if(!rec.contains(new Point2D(event.getScreenX(), event.getScreenY())))
								about.hide();
						}
					}
				});

				return listCell;
			}
		});


		englishRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				renderCharts();
			}
		});

		engineeringRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				renderCharts();
			}
		});

		realCurrentSamplesListView.getItems().addListener(sampleListChangedListener);

		leftAccordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {

			@Override
			public void changed(ObservableValue<? extends TitledPane> observable, TitledPane oldValue,
					TitledPane newValue) {
				if(newValue != null){
				}
				if(newValue != null && newValue.getText().equals("Region Of Interest")){
					showROIOnChart = true;
					renderCharts();
				}
				else{
					if(!holdROIAnnotationsCB.isSelected()){
						if(showROIOnChart){
							showROIOnChart = false;
							renderCharts();
						}
					}
				}

			}

		});



		timeScaleToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				RadioButton button = (RadioButton)newValue;
				switch(button.getText()){
				case "s":
					timeUnits.units = Unit.BASE;
					break;
				case "ms":
					timeUnits.units = Unit.MILLI;
					break;
				case "us":
					timeUnits.units = Unit.MICRO;
					break;
				case "ns":
					timeUnits.units = Unit.NANO;
					break;
				case "ps":
					timeUnits.units = Unit.PICO;
					break;
				}
				renderCharts();
			}
		});

		loadDisplacementCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				renderCharts();
			}
		});
		
		globalLoadDataFilterTextField.textProperty().addListener((a,b,c) -> {
			globalLoadDataFilterTextField.updateLabelPosition();
		});

		globalDisplacementFilterTextField.textProperty().addListener((a,b,c) -> {
			globalDisplacementFilterTextField.updateLabelPosition();
		});
		
		engineeringRadioButton.setTooltip(new Tooltip("Engineering stress and strain mode"));
		trueRadioButton.setTooltip(new Tooltip("True stress and strain mode"));
		metricRadioButton.setTooltip(new Tooltip("Metric units mode"));
		englishRadioButton.setTooltip(new Tooltip("English units mode"));
		secondsRadioButton.setTooltip(new Tooltip("Graphs time data in seconds"));
		milliSecondsRadioButton.setTooltip(new Tooltip("Graphs time data in milliseconds"));
		microSecondsRadioButton.setTooltip(new Tooltip("Graphs time data in microseconds"));
		nanoSecondsRadioButton.setTooltip(new Tooltip("Graphs time data in nanoseconds"));
		picoSecondsRadioButton.setTooltip(new Tooltip("Graphs time data in picoseconds"));
		loadDisplacementCB.setTooltip(new Tooltip("Load-Displacement mode"));
		zoomToROICB.setTooltip(new Tooltip("Zooms graph to Region of Interest (ROI)"));
		buttonOpenExportMenu.setTooltip(new Tooltip("Opens a wizard for exporting to excel and .csv"));
		tbAvgYValue.setTooltip(new Tooltip("Calculates the average Y on the ROI. If multiple ROI mode (green), it's the average of the averages"));
		tbAvgIntegralValue.setTooltip(new Tooltip("Calculates the average integral of the checked samples in the ROI"));
		tempTrimBeginRadioButton.setTooltip(new Tooltip("Temporarily trims the begin of the graphed samples. Click on any graph to trim"));
		tempTrimEndRadioButton.setTooltip(new Tooltip("Temporarily trims the end of the graphed samples. Click on any graph to trim"));
		roiSelectionModeChoiceBox.setTooltip(new Tooltip("Allows independent ROI or all samples ROI. Begin and end can be set for each sample by selecting the sample here"));
		holdROIAnnotationsCB.setTooltip(new Tooltip("Holds the ROI annotations when other tabs are opend in this accordion"));
		showSampleDirectoryButton.setTooltip(new Tooltip("Shows the sample directory, where any workspace can be selected from which samples can be loaded"));
		maxYValueTF.setTooltip(new Tooltip("The maximum Y value in the ROI (Region of Interest)"));
		durationTF.setTooltip(new Tooltip("The duration of the ROI"));
		averageMaxValueLabel.setTooltip(new Tooltip("The average of the max values in the ROI"));
		xValueLabel.setTooltip(new Tooltip("The X value of the mouse position on the graph"));
		yValueLabel.setTooltip(new Tooltip("The Y value of the mouse position on the graph"));
		radioSetBegin.setTooltip(new Tooltip("Sets the begin of the ROI. Click on the graph to use"));
		radioSetEnd.setTooltip(new Tooltip("Sets the end of the ROI. Click on the graph to use"));
		choiceBoxRoi.setTooltip(new Tooltip("Select the chart that the ROI calculations should be run on"));
		
		globalLoadDataFilterTextField.setTooltip(new Tooltip("Applies a lowpass filter to all checked datasets."));
		

	}


	
	ListChangeListener<Sample> sampleListChangedListener = new ListChangeListener<Sample>(){
		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends Sample> c) {
			renderDefaultSampleResults();
			renderSampleResults();
			renderROISelectionModeChoiceBox();
			renderCharts();
		}
	};

	public void addChartTypeListeners(){
		displayedChartListView.getCheckModel().getCheckedItems().addListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().addListener(chartTypeChangeListener);
	}

	public void removeChartTypeListeners(){
		displayedChartListView.getCheckModel().getCheckedItems().removeListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().removeListener(chartTypeChangeListener);
		displayedChartListView.getCheckModel().getCheckedItems().removeListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().removeListener(chartTypeChangeListener);
		displayedChartListView.getCheckModel().getCheckedItems().removeListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().removeListener(chartTypeChangeListener);
		displayedChartListView.getCheckModel().getCheckedItems().removeListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().removeListener(chartTypeChangeListener);
		displayedChartListView.getCheckModel().getCheckedItems().removeListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().removeListener(chartTypeChangeListener);
		displayedChartListView.getCheckModel().getCheckedItems().removeListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().removeListener(chartTypeChangeListener);
		displayedChartListView.getCheckModel().getCheckedItems().removeListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().removeListener(chartTypeChangeListener);
		displayedChartListView.getCheckModel().getCheckedItems().removeListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().removeListener(chartTypeChangeListener);
		displayedChartListView.getCheckModel().getCheckedItems().removeListener(chartTypeListener);
		displayedChartListView.getSelectionModel().selectedItemProperty().removeListener(chartTypeChangeListener);
	}

	ListChangeListener<String> chartTypeListener  = new ListChangeListener<String>() {
		public void onChanged(ListChangeListener.Change<? extends String> c) {
			renderROIChoiceBox();
			renderCharts();
		}
	};

	ChangeListener<String> chartTypeChangeListener = new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			renderROIChoiceBox();
			renderCharts();
		}
	};

	public void createRefreshListener(){
		Stage primaryStage = stage;
		primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1)
			{
				renderCharts();
			}
		});

		widthOfLeftPanel = homeSplitPane.getDividerPositions()[0] * homeSplitPane.getWidth();

		stage.getScene().widthProperty().addListener(new ChangeListener<Number>() {
			@Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				double percentage = widthOfLeftPanel / homeSplitPane.getWidth();
				homeSplitPane.setDividerPosition(0, percentage);

				if(homeSplitPane.getDividerPositions().length > 1)
					homeSplitPane.setDividerPosition(1, 1 - percentage);
			}
		});

		if(parameters != null && parameters.size() > 0) {
			try {
				String path = parameters.get(0);
				Sample sample = SPOperations.loadSample(path);
				realCurrentSamplesListView.getItems().add(sample);
				displayedChartListView.getCheckModel().checkAll();
				renderCharts();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		stage.setMaximized(true);
	}

	@FXML
	public void selectCustomDataButtonFired(){
		Stage anotherStage = new Stage();
		try {
			FXMLLoader root1 = new FXMLLoader(getClass().getResource("/net/relinc/viewer/GUI/SelectCustomData.fxml"));
			Scene scene = new Scene(root1.load());
			//scene.getStylesheets().add(getClass().getResource("/application/table-column-background.css").toExternalForm());
			anotherStage.setScene(scene);
			anotherStage.initOwner(stage);
			anotherStage.initModality(Modality.WINDOW_MODAL);
			SelectCustomDataController c = root1.<SelectCustomDataController>getController();
			c.currentSamples = getCheckedSamples();
			c.render();
			anotherStage.showAndWait();
		} catch(Exception e) {
			e.printStackTrace();
		}

		renderSampleResults();
		renderCharts();
	}

	@FXML
	public void showSampleDirectoryButtonFired(){
		sampleDirectoryGUI.showSampleDirectoryPane();
	}

	@FXML
	public void saveChartToImageButtonFired(){
		
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setDepthBuffer(true);
		WritableImage image = chartAnchorPane.snapshot(parameters, null);

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png Image(*.png)", "*.png"));
		fileChooser.setInitialFileName("*.png");
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	public void citeButtonFired(){
		System.out.println("Fired");
		Dialogs.showCitationDialog();
}
  public void loadFilterUpArrowFired(){
		double currentVal = getValueAfterArrowClick(globalLoadDataFilterTextField);
		globalLoadDataFilterTextField.setText(new DecimalFormat(".#####").format(currentVal + SPMath.getPicoArrowIncrease(currentVal, true)));
		applyGlobalLoadDataFilterButtonFired();
	}
	
	@FXML
	public void loadFilterDownArrowFired(){
		double currentVal = getValueAfterArrowClick(globalLoadDataFilterTextField);
		globalLoadDataFilterTextField.setText(new DecimalFormat(".#####").format(currentVal - SPMath.getPicoArrowIncrease(currentVal, false)));
		applyGlobalLoadDataFilterButtonFired();
	}
	
	@FXML
	public void displacementFilterUpArrowFired(){
		double currentVal = getValueAfterArrowClick(globalDisplacementFilterTextField);
		globalDisplacementFilterTextField.setText(new DecimalFormat(".#####").format(currentVal + SPMath.getPicoArrowIncrease(currentVal, true)));
		applyGlobalDisplacementDataFilterButtonFired();
	}
	
	@FXML
	public void displacementFilterDownArrowFired(){
		double currentVal = getValueAfterArrowClick(globalDisplacementFilterTextField);
		globalDisplacementFilterTextField.setText(new DecimalFormat(".#####").format(currentVal - SPMath.getPicoArrowIncrease(currentVal, false)));
		applyGlobalDisplacementDataFilterButtonFired();
	}
	
	private static double getValueAfterArrowClick(NumberTextField tf)
	{
		double currentVal = 1.;
		try{
			currentVal = Double.parseDouble(tf.getText());	
		}
		catch(NumberFormatException e)
		{
			// Failed to parse a double, stick with 1
		}
		return currentVal;
	}

	public void selectCustomRangeButtonFired(){

	}

	public void checkAllButtonFired(){
		realCurrentSamplesListView.getItems().stream().forEach(sample -> sample.selectedProperty().removeListener(sampleCheckedListener));
		realCurrentSamplesListView.getItems().forEach(s -> s.setSelected(true));
		realCurrentSamplesListView.getItems().stream().forEach(sample -> sample.selectedProperty().addListener(sampleCheckedListener));
		sampleCheckedListener.changed(null, true, false);
	}

	public void uncheckAllButtonFired(){
		realCurrentSamplesListView.getItems().stream().forEach(sample -> sample.selectedProperty().removeListener(sampleCheckedListener));
		realCurrentSamplesListView.getItems().forEach(s -> s.setSelected(false)); //
		realCurrentSamplesListView.getItems().stream().forEach(sample -> sample.selectedProperty().addListener(sampleCheckedListener));
		sampleCheckedListener.changed(null, true, false);
	}

	@FXML
	public void showVideoDialogButtonFired(){
		videoCorrelationGUI.showVideoDialog();
	}

	@FXML
	private void zoomToROICBFired(){
		renderCharts();
	}

	@FXML
	private void exportCSVButtonFired(){
		rightOptionPane.exportCSVButtonFired();
	}

	@FXML
	private void tempTrimResetButtonFired(){
		for(Sample s : getCheckedSamples()){
			DataSubset load = s.getDataSubsetAtLocation(s.results.loadDataLocation);
			DataSubset displacement = s.getDataSubsetAtLocation(s.results.displacementDataLocation);
			load.setBeginTemp(null);
			load.setEndTemp(null);
			displacement.setBeginTemp(null);
			displacement.setEndTemp(null);
		}
		renderSampleResults();
		renderCharts();
	}

	@FXML
	private void exportWorkspaceToZipButtonFired(){
		DirectoryChooser chooser = new DirectoryChooser();
		File dir = chooser.showDialog(stage);

		if(dir == null)
			return;

		File workspace = new File(treeViewHomePath).getParentFile(); 

		SPOperations.exportWorkspaceToZipFile(workspace, dir);
	}

	@FXML
	private void applyGlobalLoadDataFilterButtonFired(){
		if(globalLoadDataFilterTextField.getText().equals(""))
		{
			SPSettings.globalLoadDataLowpassFilter = null;
		}
		else
		{
			SPSettings.globalLoadDataLowpassFilter = new LowPass(globalLoadDataFilterTextField.getDouble() * 1000);
		}
		renderSampleResults();
		renderCharts();
	}

	@FXML
	private void removeGlobalLoadDataFilterButtonFired(){
		SPSettings.globalLoadDataLowpassFilter = null;
		renderSampleResults();
		renderCharts();
	}
	
	@FXML
	private void applyGlobalDisplacementDataFilterButtonFired(){
		if(globalDisplacementFilterTextField.getText().equals(""))
		{
			SPSettings.globalDisplacementDataLowpassFilter = null;
		}
		else
		{
			SPSettings.globalDisplacementDataLowpassFilter = new LowPass(globalDisplacementFilterTextField.getDouble() * 1000);
		}
		
		renderSampleResults();
		renderCharts();
	}
	
	@FXML
	private void removeGlobalDisplacementDataFilterButtonFired(){
		SPSettings.globalDisplacementDataLowpassFilter = null;
		renderSampleResults();
		renderCharts();
	}
	
	@FXML
	private void saveSessionButtonFired()
	{
		sampleDirectoryGUI.showSampleDirectoryPane();
		sampleDirectoryGUI.fireSessionsToggleButton();
		String name = Dialogs.getStringValueFromUser("Please provide a session name");
		if(name.equals(""))
		{
			return;
		}
		
		File sessionFile = new File((new File(treeViewHomePath)).getParent(), "Sessions/" + name + ".session");
		if(sessionFile.exists())
		{
			Dialogs.showAlert("Session name already used!", stage);
			return;
		}
		
		if(!sessionFile.getParentFile().exists())
			sessionFile.getParentFile().mkdir();
		Session session = new Session();
		SPOperations.writeStringToFile(session.getJSONString(this), sessionFile.getPath());
		sampleDirectoryGUI.fillSessionsListView();
	}
	
	public void applySession(File sessionFile)
	{
		renderBlock = true;
		Session session = Session.getSessionFromJSONString(SPOperations.readStringFromFile(sessionFile.getPath()));
		removeChartTypeListeners();
		realCurrentSamplesListView.getItems().removeListener(sampleListChangedListener);
		realCurrentSamplesListView.getItems().clear();
		
		if(session.globalDisplacementLowpassValue == null)
		{
			SPSettings.globalDisplacementDataLowpassFilter = null;
		}
		else{
			SPSettings.globalDisplacementDataLowpassFilter = new LowPass(session.globalDisplacementLowpassValue);
		}
		
		if(session.globalLoadLowpassValue == null)
		{
			SPSettings.globalLoadDataLowpassFilter = null;
		}
		else{
			SPSettings.globalLoadDataLowpassFilter = new LowPass(session.globalLoadLowpassValue);
		}
		
		
		
		for(SampleSession sampleSession : session.samplePaths)
		{
			Sample sample = addSampleToList(treeViewHomePath + sampleSession.path);
			renderDefaultSampleResults(); //Need to initialize sample.results
			if(sample != null)
			{
				sample.results.displacementDataLocation = sampleSession.displacementLocation;
				sample.results.loadDataLocation = sampleSession.loadLocation;
				sample.selectedProperty().removeListener(sampleCheckedListener);
				sample.setSelected(sampleSession.checked);
				sample.selectedProperty().addListener(sampleCheckedListener);
				sample.getCurrentDisplacementDatasubset().setBeginTemp(sampleSession.displacementTempTrimBeginIndex);
				sample.getCurrentDisplacementDatasubset().setEndTemp(sampleSession.displacementTempTrimEndIndex);
				sample.getCurrentLoadDatasubset().setBeginTemp(sampleSession.loadTempTrimBeginIndex);
				sample.getCurrentLoadDatasubset().setEndTemp(sampleSession.loadTempTrimEndIndex);
				sample.setBeginROITime(sampleSession.beginROITime);
				sample.setEndROITime(sampleSession.endROITime);
			}
			
		}
		renderSampleResults();
		
		

		loadDisplacementCB.setSelected(session.chartingAreaSession.loadDisplacementSelected);		
		
		if(session.chartingAreaSession.isEnglish)
			englishRadioButton.setSelected(true);
		else
			metricRadioButton.setSelected(true);
		
		if(session.chartingAreaSession.isEngineering)
			engineeringRadioButton.setSelected(true);
		else
			trueRadioButton.setSelected(true);
		
		timeScaleToggleGroup.getToggles().stream().filter(t -> ((RadioButton)t).getText().equals(session.chartingAreaSession.timeUnit)).findFirst().get().setSelected(true);
		
		sampleListChangedListener.onChanged(null); //This resets all the checked charts if load displacement sample detected
		realCurrentSamplesListView.getItems().addListener(sampleListChangedListener);
		addChartTypeListeners();
		
		session.chartingAreaSession.checkedCharts.stream().forEach(s -> displayedChartListView.getCheckModel().check(s));
		
		renderBlock = false;
		
		ROI.beginROITime = session.roiSession.beginTime;
		ROI.endROITime = session.roiSession.endTime;
		if(session.roiSession.selectedROISample != null){
			Sample s = realCurrentSamplesListView.getItems().get(getSampleIndexByName(session.roiSession.selectedROISample));
			roiSelectionModeChoiceBox.getSelectionModel().select(s);
		}
		choiceBoxRoi.getSelectionModel().select(session.roiSession.selectedData);
		holdROIAnnotationsCB.setSelected(session.roiSession.holdROIAnnotations);
		zoomToROICB.setSelected(session.roiSession.zoomToROI);
		renderCharts();
	}

	public String getDisplayedTimeUnit(){
		return ((RadioButton)timeScaleToggleGroup.getSelectedToggle()).getText();
	}
	
	private void renderDefaultSampleResults(){
		boolean loadDisplacementOnly = false;
		for(Sample sample : realCurrentSamplesListView.getItems()){
			if(!(sample instanceof HopkinsonBarSample))
				loadDisplacementOnly = true; //
			if(sample.results == null){
				sample.results = new LoadDisplacementSampleResults(sample);
			}
		}
		if(loadDisplacementOnly){
			loadDisplacementCB.setSelected(false);
			loadDisplacementCB.setDisable(true);
		}
	}

	private void setROITimeValuesToMaxRange(){
		ROI.beginROITime = 0;
		ROI.endROITime = getLowestMaxTime();
		// I beleive this should be getCheckedSamples instead of all the loaded samples.
		for(Sample s : getCheckedSamples()){
			if(s.getBeginROITime() == -1)
				s.setBeginROITime(0);
			if(s.getEndROITime() == -1 || s.getEndROITime() > s.results.time[s.results.time.length -1])
				s.setEndROITime(s.results.time[s.results.time.length - 1]);
		}
	}
	
	private ChangeListener<Boolean> sampleCheckedListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			//setROITimeValuesToMaxRange();
			renderSampleResults();
			renderROIChoiceBox(); //new command
			renderROISelectionModeChoiceBox();
			renderCharts();
		}
	};

	public Sample addSampleToList(String samplePath){
		try {
			Sample sampleToAdd = SPOperations.loadSample(samplePath);

			if(sampleToAdd != null){
				sampleToAdd.selectedProperty().addListener(sampleCheckedListener);
				SPTracker.track(SPTracker.surepulseViewerCategory, "Sample Analyzed");
				realCurrentSamplesListView.getItems().add(sampleToAdd);
				return sampleToAdd;
			}
			else{
				System.out.println("Failed to load the sample.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> getCheckedCharts()
	{
		return displayedChartListView.getCheckModel().getCheckedItems();
	}

	public void renderCharts(){
		if(loadDisplacementOnlySampleExists(getCheckedSamples())){
			loadDisplacementCB.setSelected(true);
			loadDisplacementCB.setDisable(true);
		}
		else{
			loadDisplacementCB.setDisable(false);
		}

		renderDisplayedChartListViewChartOptions();

		if(renderBlock)
			return;
		
		chartAnchorPane.getChildren().clear();
		renderROIResults();
		ArrayList<LineChart<Number, Number>> charts = new ArrayList<LineChart<Number, Number>>();
		if(vBoxHoldingCharts.getChildren().size() > 1){
			if(displayedChartListView.getCheckModel().getCheckedItems().size() == 0)
			{
				// All the samples have been unchecked. The video/images need to be cleared.
				videoCorrelationGUI.removeVideoControls();
				videoCorrelationGUI.imagePaths = new ArrayList<>();
				imageView.setImage(null);
				return;
			}
			
			if(getCheckedSamples().size() != 1)
			{
				// Video only supports one sample. Clear and remove if not 1
				videoCorrelationGUI.removeVideoControls();
				videoCorrelationGUI.imagePaths = new ArrayList<>();
				imageView.setImage(null);
				return;
			}
			

			//video dialog is open.
			LineChartWithMarkers<Number, Number> chart = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(0));
			imageMatchingChart = chart;
			
			VBox vBox = new VBox();
			vBox.getChildren().add(chart);
			vBox.getChildren().add(imageView);
			vBox.setAlignment(Pos.CENTER);


			VBox.setVgrow(chart, Priority.ALWAYS);
			chartAnchorPane.getChildren().add(vBox);
			AnchorPane.setTopAnchor(vBox, 0.0);
			AnchorPane.setBottomAnchor(vBox, 0.0);
			AnchorPane.setLeftAnchor(vBox, 0.0);
			AnchorPane.setRightAnchor(vBox, 0.0);
			charts.add(chart);
		}
		else if (displayedChartListView.getCheckModel().getCheckedItems().size() == 0) {
			if (displayedChartListView.getSelectionModel().getSelectedIndex() != -1) {
				LineChart<Number, Number> chart = getChart(displayedChartListView.getSelectionModel().getSelectedItem());
				if(chart == null)
					return;
				chartAnchorPane.getChildren().add(chart);
				AnchorPane.setTopAnchor(chart, 0.0);
				AnchorPane.setBottomAnchor(chart, 0.0);
				AnchorPane.setLeftAnchor(chart, 0.0);
				AnchorPane.setRightAnchor(chart, 0.0);
				charts.add(chart);
			}
		}
		else if(displayedChartListView.getCheckModel().getCheckedItems().size() == 1){
			LineChart<Number, Number> chart = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(0));
			chart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
			chartAnchorPane.getChildren().add(chart);
			AnchorPane.setTopAnchor(chart, 0.0);
			AnchorPane.setBottomAnchor(chart, 0.0);
			AnchorPane.setLeftAnchor(chart, 0.0);
			AnchorPane.setRightAnchor(chart, 0.0);
			charts.add(chart);
		}
		else if(displayedChartListView.getCheckModel().getCheckedItems().size() == 2){
			LineChart<Number, Number> chart = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(0));
			LineChart<Number, Number> chart2 = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(1));
			VBox vBox = new VBox();
			vBox.getChildren().add(chart);
			vBox.getChildren().add(chart2);
			VBox.setVgrow(chart, Priority.ALWAYS);
			VBox.setVgrow(chart2, Priority.ALWAYS);
			chartAnchorPane.getChildren().add(vBox);
			AnchorPane.setTopAnchor(vBox, 0.0);
			AnchorPane.setBottomAnchor(vBox, 0.0);
			AnchorPane.setLeftAnchor(vBox, 0.0);
			AnchorPane.setRightAnchor(vBox, 0.0);
			charts.add(chart);
			charts.add(chart2);
		}
		else if(displayedChartListView.getCheckModel().getCheckedItems().size() == 3){
			LineChart<Number, Number> chart = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(0));
			LineChart<Number, Number> chart2 = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(1));
			LineChart<Number, Number> chart3 = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(2));
			chart.setTitle(displayedChartListView.getCheckModel().getCheckedItems().get(0));
			chart2.setTitle(displayedChartListView.getCheckModel().getCheckedItems().get(1));
			chart3.setTitle(displayedChartListView.getCheckModel().getCheckedItems().get(2));
			VBox vBox = new VBox();
			HBox topHBox = new HBox();
			vBox.getChildren().add(chart);
			vBox.getChildren().add(topHBox);
			VBox.setVgrow(chart, Priority.ALWAYS);
			VBox.setVgrow(topHBox, Priority.ALWAYS);
			HBox.setHgrow(chart2, Priority.ALWAYS);
			HBox.setHgrow(chart3, Priority.ALWAYS);
			topHBox.getChildren().add(chart2);
			topHBox.getChildren().add(chart3);
			chartAnchorPane.getChildren().add(vBox);
			AnchorPane.setTopAnchor(vBox, 0.0);
			AnchorPane.setBottomAnchor(vBox, 0.0);
			AnchorPane.setLeftAnchor(vBox, 0.0);
			AnchorPane.setRightAnchor(vBox, 0.0);
			charts.add(chart);
			charts.add(chart2);
			charts.add(chart3);
		}
		else if(displayedChartListView.getCheckModel().getCheckedItems().size() == 4){
			LineChart<Number, Number> chart = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(0));
			LineChart<Number, Number> chart2 = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(1));
			LineChart<Number, Number> chart3 = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(2));
			LineChart<Number, Number> chart4 = getChart(displayedChartListView.getCheckModel().getCheckedItems().get(3));
			HBox hBox = new HBox();
			VBox leftVBox = new VBox();
			VBox rightVBox = new VBox();
			AnchorPane.setTopAnchor(hBox, 0.0);
			AnchorPane.setBottomAnchor(hBox, 0.0);
			AnchorPane.setLeftAnchor(hBox, 0.0);
			AnchorPane.setRightAnchor(hBox, 0.0);
			HBox.setHgrow(leftVBox, Priority.ALWAYS);
			HBox.setHgrow(rightVBox, Priority.ALWAYS);
			VBox.setVgrow(chart, Priority.ALWAYS);
			VBox.setVgrow(chart2, Priority.ALWAYS);
			VBox.setVgrow(chart3, Priority.ALWAYS);
			VBox.setVgrow(chart4, Priority.ALWAYS);

			hBox.getChildren().add(leftVBox);
			hBox.getChildren().add(rightVBox);
			leftVBox.getChildren().add(chart);
			leftVBox.getChildren().add(chart3);
			rightVBox.getChildren().add(chart2);
			rightVBox.getChildren().add(chart4);
			chartAnchorPane.getChildren().add(hBox);
			charts.add(chart);
			charts.add(chart2);
			charts.add(chart3);
			charts.add(chart4);
		}
		else{
			System.out.println("NONE OF THE CHARTING OPTIONS WERE VALID");
		}
		charts.stream().forEach(c -> c.setAxisSortingPolicy(LineChart.SortingPolicy.NONE));
	}


	private void renderDisplayedChartListViewChartOptions() {
		String loadVsDisplacementName = "Load Vs Displacement";
		String loadVsTimeName = "Load Vs Time";
		String displacementVsTimeName = "Displacement Vs Time";
		String displacementRateVsTimeName = "Displacement Rate Vs Time";
		
		String stressVsStrainName = "Stress Vs Strain";
		String faceForceVsTimeName = "Face Force Vs Time";
		String stressVsTimeName = "Stress Vs Time";
		String strainVsTimeName = "Strain Vs Time";
		String strainRateVsTimeName = "Strain Rate Vs Time";
		
		if(loadDisplacementCB.isSelected()){
			if(displayedChartListView.getItems().size() > 0 && displayedChartListView.getItems().get(0).equals(loadVsDisplacementName)){
				return;
			}
			removeChartTypeListeners();
			displayedChartListView.getSelectionModel().clearSelection();
			displayedChartListView.getCheckModel().clearChecks();
			displayedChartListView.getItems().clear();
			displayedChartListView.getItems().add(loadVsDisplacementName);
			displayedChartListView.getItems().add(loadVsTimeName);
			displayedChartListView.getItems().add(displacementVsTimeName);
			displayedChartListView.getItems().add(displacementRateVsTimeName);
			addChartTypeListeners();
		}
		else{
			
			if(displayedChartListView.getItems().size() > 0 && displayedChartListView.getItems().get(0).equals(stressVsStrainName)){
				removeChartTypeListeners();
				boolean forceIsApplicable = true;
				for(Sample s : getCheckedSamples()){
					if(!(s.isFaceForceGraphable())){
						forceIsApplicable = false;
					}
				}
				if(!forceIsApplicable){
					if (displayedChartListView.getItems().contains(faceForceVsTimeName)) {
						displayedChartListView.getCheckModel().clearChecks();
						displayedChartListView.getSelectionModel().clearSelection();
						displayedChartListView.getItems().clear();

						displayedChartListView.getItems().add(stressVsStrainName);
						displayedChartListView.getItems().add(stressVsTimeName);
						displayedChartListView.getItems().add(strainVsTimeName);
						displayedChartListView.getItems().add(strainRateVsTimeName);
					}
				}
				else{
					if(!displayedChartListView.getItems().contains(faceForceVsTimeName))
						displayedChartListView.getItems().add(faceForceVsTimeName);
				}
				addChartTypeListeners();
				return;
			}
			
			removeChartTypeListeners();
			displayedChartListView.getCheckModel().clearChecks();
			displayedChartListView.getSelectionModel().clearSelection();
			displayedChartListView.getItems().clear();

			displayedChartListView.getItems().add(stressVsStrainName);
			displayedChartListView.getItems().add(stressVsTimeName);
			displayedChartListView.getItems().add(strainVsTimeName);
			displayedChartListView.getItems().add(strainRateVsTimeName);
			addChartTypeListeners();
			
			boolean forceIsApplicable = true;
			for(Sample s : getCheckedSamples()){
				if(!(s.getCurrentLoadDatasubset() instanceof TransmissionPulse && s.getCurrentDisplacementDatasubset() instanceof ReflectedPulse)){
					forceIsApplicable = false;
				}
			}
			
			if(forceIsApplicable){
				displayedChartListView.getItems().add(faceForceVsTimeName);
			}
			
			addChartTypeListeners();
		}
	}

	private boolean loadDisplacementOnlySampleExists(List<Sample> checkedSamples) {
		for(Sample s : checkedSamples){
			if(!(s instanceof HopkinsonBarSample))
				return true;
		}
		return false;
	}

	private void renderROIResults() {

		averageNValueTF.setText("");
		averageKValueTF.setText("");
		durationTF.setText("");
		tbAvgYValue.setText("");
		tbAvgIntegralValue.setText("");
		maxYValueTF.setText("");
		
		ROI.renderROIResults(getCheckedSamples(), loadDisplacementCB.isSelected(), roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem());

		//average value
		if(getCheckedSamples().size() == 0){
			return;
		}
		//now there's at least one checked sample

		String chartOfInterest = "";

		if(choiceBoxRoi.getSelectionModel().getSelectedItem() == null){
			//nothing selected, look at chart
			//chartOfInterest = displayedChartListView.getCheckModel().getCheckedItems().size() > 0 ? displayedChartListView.getCheckModel().getCheckedItems().get(0) : null;
		}
		else{
			chartOfInterest = choiceBoxRoi.getSelectionModel().getSelectedItem();
		}

		if(chartOfInterest == null || chartOfInterest.equals("")){
			return;
		}

		durationTF.setText(Double.toString(SPOperations.round((ROI.endROITime - ROI.beginROITime) * timeUnits.getMultiplier(),5)));

		if(chartOfInterest.equals("Stress Vs Time")){
			if(getCheckedSamples().size() > 1){
				averageYValueLabel.setText("Average Stress");
				averageMaxValueLabel.setText("Average Max Stress");
			}
			else{
				averageYValueLabel.setText("Average Stress");
				averageMaxValueLabel.setText("Max Stress");
			}
			double avg = ROI.averageEngineeringStress;
			double avgMax = ROI.averageMaxEngineeringStress;
			double integral = ROI.averageEngineeringStressVsTimeIntegral;
			if (trueRadioButton.isSelected()){
				avg = ROI.averageTrueStress;
				avgMax = ROI.averageMaxTrueStress;
				integral = ROI.averageTrueStressVsTimeIntegral;
			}


			if (englishRadioButton.isSelected()){
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.ksiFromPa(avg),4)));

				tbAvgIntegralValue.setText(Double.toString(
						SPOperations.round(Converter.ksiFromPa(integral), 4)));

				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.ksiFromPa(avgMax),4)));
			}
			else
			{
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.MpaFromPa(avg),4)));

				tbAvgIntegralValue.setText(Double.toString(
						SPOperations.round(Converter.MpaFromPa(integral), 4)));

				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.MpaFromPa(avgMax),4)));
			}

		}
		else if(chartOfInterest.equals("Strain Vs Time")){
			if(getCheckedSamples().size() > 1){
				averageYValueLabel.setText("Average Strain");
				averageMaxValueLabel.setText("Average Max Strain");
			}
			else{
				averageYValueLabel.setText("Average Strain");
				averageMaxValueLabel.setText("Max Strain");
			}
			double avg = ROI.averageEngineeringStrain;
			double avgMax = ROI.averageMaxEngineeringStrain;
			double integral = ROI.averageEngineeringStrainVsTimeIntegral;
			if(trueRadioButton.isSelected()){
				avg = ROI.averageTrueStrain;
				integral = ROI.averageTrueStrainVsTimeIntegral;
				avgMax = ROI.averageMaxTrueStrain;
			}

			tbAvgYValue.setText(Double.toString(SPOperations.round(avg,4)));
			tbAvgIntegralValue.setText(Double.toString(SPOperations.round(integral, 4)));
			maxYValueTF.setText(Double.toString(SPOperations.round(avgMax,4)));
		}
		else if(chartOfInterest.equals("Stress Vs Strain")){
			if(getCheckedSamples().size() >1){
				averageYValueLabel.setText("Average Stress");
				averageMaxValueLabel.setText("Average Max Stress");
			}
			else{
				averageYValueLabel.setText("Average Stress");
				averageMaxValueLabel.setText("Max Stress");
			}
			double avg = ROI.averageEngineeringStress;
			double integral = ROI.averageEngineeringStressVsStrainIntegral;
			double avgMax = ROI.averageMaxEngineeringStress;
			double avgKVal = ROI.averageEngKValue;
			double avgNVal = ROI.averageEngNValue;
			if (trueRadioButton.isSelected()){
				avg = ROI.averageTrueStress;
				integral = ROI.averageTrueStressVsStrainIntegral;
				avgMax = ROI.averageMaxTrueStress;
				avgKVal = ROI.averageTrueKValue;
				avgNVal = ROI.averageTrueNValue;
			}

			if (englishRadioButton.isSelected()){
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.ksiFromPa(avg),4)));

				tbAvgIntegralValue.setText(Double.toString(
						SPOperations.round(Converter.ksiFromPa(integral), 4)));

				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.ksiFromPa(avgMax),4)));
				
				averageKValueTF.setText(Double.toString(SPOperations.round(Converter.ksiFromPa(avgKVal), 4)));
				averageNValueTF.setText(Double.toString(SPOperations.round(avgNVal, 4)));
			}
			else{
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.MpaFromPa(avg),4)));

				tbAvgIntegralValue.setText(Double.toString(
						SPOperations.round(Converter.MpaFromPa(integral), 4)));

				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.MpaFromPa(avgMax),4)));
				
				averageKValueTF.setText(Double.toString(SPOperations.round(Converter.MpaFromPa(avgKVal), 4)));
				averageNValueTF.setText(Double.toString(SPOperations.round(avgNVal, 4)));
			}
		}
		else if(chartOfInterest.equals("Strain Rate Vs Time")){
			//String avg = getCheckedSamples().size() > 1 ? "Average" : "";//cool code
			if(getCheckedSamples().size() > 1){
				averageYValueLabel.setText("Average Strain Rate");
				averageMaxValueLabel.setText("Average Max Strain Rate");
			}
			else{
				averageYValueLabel.setText("Average Strain Rate");
				averageMaxValueLabel.setText("Max Strain Rate");
			}
			double avg = ROI.averageEngineeringStrainRate;
			double integral = ROI.averageEngineeringStrainRateVsTimeIntegral;
			double avgMax = ROI.averageMaxEngineeringStrainRate;
			if(trueRadioButton.isSelected()){
				avg = ROI.averageTrueStrainRate;
				integral = ROI.averageTrueStrainRateVsTimeIntegral;
				avgMax = ROI.averageMaxTrueStrainRate;
			}


			tbAvgYValue.setText(Double.toString(SPOperations.round(avg, 4)));
			tbAvgIntegralValue.setText(Double.toString(SPOperations.round(integral, 4)));
			maxYValueTF.setText(Double.toString(SPOperations.round(avgMax, 4)));
		}
		else if(chartOfInterest.equals("Displacement Vs Time")){
			if(getCheckedSamples().size() > 1){
				averageYValueLabel.setText("Average Displacement");
				averageMaxValueLabel.setText("Average Max Displacement");
			}
			else{
				averageYValueLabel.setText("Average Displacement");
				averageMaxValueLabel.setText("Max Displacement");
			}
			double avg = ROI.averageDisplacement;
			double avgMax = ROI.averageMaxDisplacement;
			double integral = ROI.averageDisplacementVsTimeIntegral;

			if(englishRadioButton.isSelected())
			{
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.InchFromMeter(avg),4)));
				tbAvgIntegralValue.setText(Double.toString(SPOperations.round(Converter.InchFromMeter(integral), 4)));
				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.InchFromMeter(avgMax),4)));
			} else {
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.mmFromM(avg),4)));
				tbAvgIntegralValue.setText(Double.toString(SPOperations.round(Converter.mmFromM(integral), 4)));
				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.mmFromM(avgMax),4)));
			}
			
		}
		else if(chartOfInterest.equals("Displacement Rate Vs Time")){
			if(getCheckedSamples().size() > 1){
				averageYValueLabel.setText("Average Displacement Rate");
				averageMaxValueLabel.setText("Average Max Displacement Rate");
			}
			else{
				averageYValueLabel.setText("Average Displacement Rate");
				averageMaxValueLabel.setText("Max Displacement Rate");
			}
			double avg = ROI.averageDisplacementRate;
			double avgMax = ROI.averageMaxDisplacementRate;
			double integral = ROI.averageDisplacementRateVsTimeIntegral;

			if(englishRadioButton.isSelected())
			{
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.InchFromMeter(avg),4)));
				tbAvgIntegralValue.setText(Double.toString(SPOperations.round(Converter.InchFromMeter(integral), 4)));
				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.InchFromMeter(avgMax),4)));
			} else {
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.mmFromM(avg),4)));
				tbAvgIntegralValue.setText(Double.toString(SPOperations.round(Converter.mmFromM(integral), 4)));
				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.mmFromM(avgMax),4)));
			}
			
		}
		else if(chartOfInterest.equals("Load Vs Time")){
			if(getCheckedSamples().size() > 1){
				averageYValueLabel.setText("Average Load");
				averageMaxValueLabel.setText("Average Max Load");
			}
			else{
				averageYValueLabel.setText("Average Load");
				averageMaxValueLabel.setText("Max Load");
			}
			double avg = ROI.averageLoad;
			double avgMax = ROI.averageMaxLoad;
			double integral = ROI.averageLoadVsTimeIntegral;

			if (englishRadioButton.isSelected()){
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.LbfFromN(avg),4)));
				tbAvgIntegralValue.setText(Double.toString(
						SPOperations.round(Converter.LbfFromN(integral), 4)));

				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.LbfFromN(avgMax),4)));
			}
			else
			{
				tbAvgYValue.setText(Double.toString(SPOperations.round(avg,4)));

				tbAvgIntegralValue.setText(Double.toString(
						SPOperations.round(integral, 4)));

				maxYValueTF.setText(Double.toString(SPOperations.round(avgMax,4)));
			}
		}
		else if(chartOfInterest.equals("Load Vs Displacement")){
			if(getCheckedSamples().size() >1){
				averageYValueLabel.setText("Average Load");
				averageMaxValueLabel.setText("Average Max Load");
			}
			else{
				averageYValueLabel.setText("Average Load");
				averageMaxValueLabel.setText("Max Load");
			}
			double avg = ROI.averageLoad;
			double integral = ROI.averageLoadVsDisplacementIntegral;
			double avgMax = ROI.averageMaxLoad;
			double avgKVal = ROI.averageLoadKValue;
			double avgNVal = ROI.averageLoadNValue;

			if (englishRadioButton.isSelected()){
				tbAvgYValue.setText(Double.toString(SPOperations.round(Converter.LbfFromN(avg),4)));

				tbAvgIntegralValue.setText(Double.toString(
						SPOperations.round(Converter.LbfFromN(integral), 4)));

				maxYValueTF.setText(Double.toString(SPOperations.round(Converter.LbfFromN(avgMax),4)));
				
				averageKValueTF.setText(Double.toString(SPOperations.round(Converter.LbfFromN(avgKVal), 4)));
				averageNValueTF.setText(Double.toString(SPOperations.round(avgNVal, 4)));
			}
			else{
				tbAvgYValue.setText(Double.toString(SPOperations.round(avg,4)));

				tbAvgIntegralValue.setText(Double.toString(
						SPOperations.round(integral, 4)));

				maxYValueTF.setText(Double.toString(SPOperations.round(avgMax,4)));
				
				averageKValueTF.setText(Double.toString(SPOperations.round(avgKVal, 4)));
				averageNValueTF.setText(Double.toString(SPOperations.round(avgNVal, 4)));
			}
		}
	}

	public int getSampleIndexByName(String sampleName){
		for(int i = 0; i < realCurrentSamplesListView.getItems().size(); i++){
					if(sampleName.equals(realCurrentSamplesListView.getItems().get(i).getName()))
						return i;
				}
				return -1;
	}

	private LineChartWithMarkers<Number, Number> getChart(String selectedItem) {
		LineChartWithMarkers<Number, Number> chart = null;
		switch (selectedItem){
		case "Stress Vs Strain":
			chart = getStressStrainChart();
			break;
		case "Stress Vs Time":
			chart = getStressTimeChart();
			break;
		case "Strain Vs Time":
			chart = getStrainTimeChart();
			break;
		case "Strain Rate Vs Time":
			chart = getStrainRateTimeChart();
			break;
		case "Face Force Vs Time":
			chart = getFaceForceTimeChart();
			break;
		case "Energy Vs Time":
			chart = getEnergyTimeChart();
			break;
		case "Load Vs Displacement":
			chart = getLoadDisplacementChart();
			break;
		case "Load Vs Time":
			chart = getLoadTimeChart();
			break;
		case "Displacement Vs Time":
			chart = getDisplacementTimeChart();
			break;
		case "Displacement Rate Vs Time":
			chart = getDisplacementRateTimeChart();

		}
		return chart;
	}

	private LineChartWithMarkers<Number, Number> getDisplacementRateTimeChart() {
		LineChartWithMarkers<Number, Number> chart = chartsGUI.getDisplacementRateTimeChart();
		addROIFunctionalityToTimeChart(chart);
		addXYListenerToChart(chart);
		return chart;
	}

	private LineChartWithMarkers<Number, Number> getDisplacementTimeChart() {
		LineChartWithMarkers<Number, Number> chart = chartsGUI.getDisplacementTimeChart();
		addROIFunctionalityToTimeChart(chart);
		addXYListenerToChart(chart);
		return chart;
	}

	private LineChartWithMarkers<Number, Number> getLoadTimeChart() {
		LineChartWithMarkers<Number, Number> chart = chartsGUI.getLoadTimeChart();
		addROIFunctionalityToTimeChart(chart);
		addXYListenerToChart(chart);
		return chart;
	}

	private LineChartWithMarkers<Number, Number> getLoadDisplacementChart() {
		LineChartWithMarkers<Number, Number> chart = chartsGUI.getLoadDisplacementChart();
		
		if(showROIOnChart){
			chart.clearVerticalMarkers();
			Sample roiSample = roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem();
			if (roiSample == null || roiSample.placeHolderSample) {
				if (getCheckedSamples().size() == 1) {
					Sample s = getCheckedSamples().get(0);
					int beginIndex = SPOperations.findFirstIndexGreaterorEqualToValue(s.results.time, ROI.beginROITime);
					int endIndex = SPOperations.findFirstIndexGreaterorEqualToValue(s.results.time, ROI.endROITime);

					if (englishRadioButton.isSelected()) {
						chart.addVerticalRangeMarker(
								new Data<Number, Number>(s.results.getDisplacement("in")[beginIndex],
										s.results.getDisplacement("in")[endIndex]),
								Color.GREEN);
					} else {
						chart.addVerticalRangeMarker(
								new Data<Number, Number>(s.results.getDisplacement("mm")[beginIndex],
										s.results.getDisplacement("mm")[endIndex]),
								Color.GREEN);
					}

				}
			} 
			else {
				for(Sample s : getCheckedSamples()){
					int beginIndex = SPOperations.findFirstIndexGreaterorEqualToValue(s.results.time, s.getBeginROITime());
					int endIndex = SPOperations.findFirstIndexGreaterorEqualToValue(s.results.time, s.getEndROITime());

					if (englishRadioButton.isSelected()) {
						chart.addVerticalValueMarker(
								new Data<Number, Number>(s.results.getDisplacement("in")[beginIndex],
										0),
								getSampleChartColor(s));
						chart.addVerticalValueMarker(
								new Data<Number, Number>(s.results.getDisplacement("in")[endIndex],
										0),
								getSampleChartColor(s));
					} else {
						chart.addVerticalValueMarker(
								new Data<Number, Number>(s.results.getDisplacement("mm")[beginIndex],
										0),
								getSampleChartColor(s));
						chart.addVerticalValueMarker(
								new Data<Number, Number>(s.results.getDisplacement("mm")[endIndex],
										0),
								getSampleChartColor(s));
					}
				}
			}

		}

		//set click listener
		chart.lookup(".chart-plot-background").setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				
				if (dataModifiersTitledPane.isExpanded()) {
					
					double displacementValue = (double) chart.getXAxis().getValueForDisplay(mouseEvent.getX());
					int index = 0;
					
					double longestStrain = -Double.MAX_VALUE;
					Sample sam = null;
					for(Sample s : getCheckedSamples())
					{
						if(s.getCurrentDisplacementDatasubset().getTrimmedData()[s.getCurrentDisplacementDatasubset().getTrimmedData().length - 1] > longestStrain)
						{
							longestStrain = s.getCurrentDisplacementDatasubset().getTrimmedData()[s.getCurrentDisplacementDatasubset().getTrimmedData().length - 1];
							sam = s;
						}
					}
					
					if (englishRadioButton.isSelected())
						index = SPOperations.findFirstIndexGreaterorEqualToValue(sam.results.getDisplacement("in"),
								displacementValue);
					else
						index = SPOperations.findFirstIndexGreaterorEqualToValue(sam.results.getDisplacement("mm"),
								displacementValue);
					double timeValue = sam.results.time[index];
					if (tempTrimBeginRadioButton.isSelected()) {
						for (Sample s : getCheckedSamples()) {
							DataSubset displacementData = s.getDataSubsetAtLocation(s.results.displacementDataLocation);
							DataSubset loadData = s.getDataSubsetAtLocation(s.results.loadDataLocation);
							displacementData.setBeginTempFromTimeValue(timeValue + displacementData.Data.timeData[displacementData.getBegin()]);
							loadData.setBeginTempFromTimeValue(timeValue + loadData.Data.timeData[loadData.getBegin()]);
						}
					} else if (tempTrimEndRadioButton.isSelected()) {
						for (Sample s : getCheckedSamples()) {
							DataSubset displacementData = s.getDataSubsetAtLocation(s.results.displacementDataLocation);
							DataSubset loadData = s.getDataSubsetAtLocation(s.results.loadDataLocation);
							displacementData.setEndTempFromTimeValue(timeValue + displacementData.Data.timeData[displacementData.getBegin()]);
							loadData.setEndTempFromTimeValue(timeValue + loadData.Data.timeData[loadData.getBegin()]);
						}
					}
					renderSampleResults();
				} else if (regionOfInterestTitledPane.isExpanded()) {

					Sample roiSample = roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem();
					if (roiSample == null || roiSample.placeHolderSample) {
						// placeHolderSample is not a real sample, just a placeholder to signify all samples are in ROI.
						if (getCheckedSamples().size() != 1)
							return;
						Sample sam = getCheckedSamples().get(0);
						double strainValue = (double) chart.getXAxis().getValueForDisplay(mouseEvent.getX());
						int index = 0;

						if (englishRadioButton.isSelected())
							index = SPOperations.findFirstIndexGreaterorEqualToValue(sam.results.getDisplacement("in"),
									strainValue);
						else
							index = SPOperations.findFirstIndexGreaterorEqualToValue(sam.results.getDisplacement("mm"),
									strainValue);

						double timeValue = sam.results.time[index];

						if (radioSetBegin.isSelected()) {
							if (timeValue > 0 && timeValue < ROI.endROITime) {
								ROI.beginROITime = timeValue;
							}
						} else if (radioSetEnd.isSelected()) {
							if (timeValue < getLowestMaxTime() && timeValue > ROI.beginROITime) {
								ROI.endROITime = timeValue;
							}
						}
					} else {
						double strainValue = (double) chart.getXAxis().getValueForDisplay(mouseEvent.getX());
						int index = 0;

						if (englishRadioButton.isSelected())
							index = SPOperations.findFirstIndexGreaterorEqualToValue(
									roiSample.results.getDisplacement("in"), strainValue);
						else
							index = SPOperations.findFirstIndexGreaterorEqualToValue(
									roiSample.results.getDisplacement("mm"), strainValue);

						if (index != -1) {
							double timeValue = roiSample.results.time[index];
							if (radioSetBegin.isSelected()) {
								if (timeValue > 0 && timeValue < roiSample.getEndROITime()) {
									roiSample.setBeginROITime(timeValue);
								}
							} else if (radioSetEnd.isSelected()) {
								if (timeValue < roiSample.results.time[roiSample.results.time.length - 1]
										&& timeValue > roiSample.getBeginROITime()) {
									roiSample.setEndROITime(timeValue);
								}
							}
						}
					}
				}

				renderCharts();
			}
		});

		chart.lookup(".chart-plot-background").setOnMouseMoved(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				double xValue = (double) chart.getXAxis().getValueForDisplay(event.getX());
				double yValue = (double) chart.getYAxis().getValueForDisplay(event.getY());
				xValueLabel.setText("X: " + SPOperations.round(xValue, 4));
				yValueLabel.setText("Y: " + SPOperations.round(yValue,4));
			}

		});
		return chart;
	}

	private LineChartWithMarkers<Number, Number> getEnergyTimeChart() {
		// TODO Auto-generated method stub
		return null;
	}

	private LineChartWithMarkers<Number, Number> getFaceForceTimeChart() {
		LineChartWithMarkers<Number, Number> chart = chartsGUI.getFaceForceTimeChart();
		addROIFunctionalityToTimeChart(chart);
		addXYListenerToChart(chart);
		return chart;
	}

	private LineChartWithMarkers<Number, Number> getStrainRateTimeChart() {
		LineChartWithMarkers<Number, Number> chart = chartsGUI.getStrainRateTimeChart();
		addROIFunctionalityToTimeChart(chart);
		addXYListenerToChart(chart);
		return chart;
	}

	private LineChartWithMarkers<Number, Number> getStrainTimeChart() {
		LineChartWithMarkers<Number, Number> chart = chartsGUI.getStrainTimeChart();
		addROIFunctionalityToTimeChart(chart);
		addXYListenerToChart(chart);
		return chart;
	}

	private void renderSampleResults(){
		//renders the result object for each sample
		for(Sample sample : getCheckedSamples()){
			sample.results.render();
		}
		setROITimeValuesToMaxRange();
	}

	private LineChartWithMarkers<Number, Number> getStressTimeChart() {
		LineChartWithMarkers<Number, Number> chart = chartsGUI.getStressTimeChart();
		addROIFunctionalityToTimeChart(chart);
		addXYListenerToChart(chart);
		return chart;
	}

	private LineChartWithMarkers<Number, Number> getStressStrainChart() {
		LineChartWithMarkers<Number, Number> chart = chartsGUI.getStressStrainChart();
		
		// The ROI and click listener are different for Stress/Strain because Strain is on the x axis instead of time.
		if(showROIOnChart){
			chart.clearVerticalMarkers();
			Sample roiSample = roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem();
			if (roiSample == null || roiSample.placeHolderSample) {
				if (getCheckedSamples().size() == 1) {
					Sample s = getCheckedSamples().get(0);
					int beginIndex = SPOperations.findFirstIndexGreaterorEqualToValue(s.results.time, ROI.beginROITime);
					int endIndex = SPOperations.findFirstIndexGreaterorEqualToValue(s.results.time, ROI.endROITime);

					if (engineeringRadioButton.isSelected()) {
						chart.addVerticalRangeMarker(
								new Data<Number, Number>(s.results.getEngineeringStrain()[beginIndex],
										s.results.getEngineeringStrain()[endIndex]),
								Color.GREEN);
					} else {
						chart.addVerticalRangeMarker(new Data<Number, Number>(s.results.getTrueStrain()[beginIndex],
								s.results.getTrueStrain()[endIndex]), Color.GREEN);
					}

				}
			}
			else{
				for(Sample s : getCheckedSamples()){
					int beginIndex = SPOperations.findFirstIndexGreaterorEqualToValue(s.results.time, s.getBeginROITime());
					int endIndex = SPOperations.findFirstIndexGreaterorEqualToValue(s.results.time, s.getEndROITime());
					if (engineeringRadioButton.isSelected()) {
						chart.addVerticalValueMarker(
								new Data<Number, Number>(s.results.getEngineeringStrain()[beginIndex],
										0),
								getSampleChartColor(s));
						chart.addVerticalValueMarker(
								new Data<Number, Number>(s.results.getEngineeringStrain()[endIndex],
										0),
								getSampleChartColor(s));
					} else {
						chart.addVerticalValueMarker(
								new Data<Number, Number>(s.results.getTrueStrain()[beginIndex],
										0),
								getSampleChartColor(s));
						chart.addVerticalValueMarker(
								new Data<Number, Number>(s.results.getTrueStrain()[endIndex],
										0),
								getSampleChartColor(s));
					}
				}
			}

		}

		//set click listener
		chart.lookup(".chart-plot-background").setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				
				if (dataModifiersTitledPane.isExpanded()) {
					double strainValue = (double) chart.getXAxis().getValueForDisplay(mouseEvent.getX());
					int index = 0;
					
					//Get the sample with the largest strain value.
					double longestStrain = -Double.MAX_VALUE;
					Sample sam = null;
					for(Sample s : getCheckedSamples())
					{
						if(s.getCurrentDisplacementDatasubset().getTrimmedData()[s.getCurrentDisplacementDatasubset().getTrimmedData().length - 1] > longestStrain)
						{
							longestStrain = s.getCurrentDisplacementDatasubset().getTrimmedData()[s.getCurrentDisplacementDatasubset().getTrimmedData().length - 1];
							sam = s;
						}
					}
					
					if (trueRadioButton.isSelected())
						index = SPOperations.findFirstIndexGreaterorEqualToValue(sam.results.getTrueStrain(),
								strainValue);
					else
						index = SPOperations.findFirstIndexGreaterorEqualToValue(sam.results.getEngineeringStrain(),
								strainValue);

					double timeValue = sam.results.time[index];
					
					if (tempTrimBeginRadioButton.isSelected()) {
						for (Sample s : getCheckedSamples()) {
							DataSubset displacementData = s.getDataSubsetAtLocation(s.results.displacementDataLocation);
							DataSubset loadData = s.getDataSubsetAtLocation(s.results.loadDataLocation);
							displacementData.setBeginTempFromTimeValue(timeValue + displacementData.Data.timeData[displacementData.getBegin()]);
							loadData.setBeginTempFromTimeValue(timeValue + loadData.Data.timeData[loadData.getBegin()]);
						}
					} else if (tempTrimEndRadioButton.isSelected()) {
						for (Sample s : getCheckedSamples()) {
							DataSubset displacementData = s.getDataSubsetAtLocation(s.results.displacementDataLocation);
							DataSubset loadData = s.getDataSubsetAtLocation(s.results.loadDataLocation);
							displacementData.setEndTempFromTimeValue(timeValue + displacementData.Data.timeData[displacementData.getBegin()]);
							loadData.setEndTempFromTimeValue(timeValue + loadData.Data.timeData[loadData.getBegin()]);
						}
					}
					renderSampleResults();
				} else if (regionOfInterestTitledPane.isExpanded()) {

					Sample roiSample = roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem();
					if (roiSample == null || roiSample.placeHolderSample) {
						if (getCheckedSamples().size() != 1)
							return;
						Sample sam = getCheckedSamples().get(0);
						double strainValue = (double) chart.getXAxis().getValueForDisplay(mouseEvent.getX());
						int index = 0;

						if (trueRadioButton.isSelected())
							index = SPOperations.findFirstIndexGreaterorEqualToValue(sam.results.getTrueStrain(),
									strainValue);
						else
							index = SPOperations.findFirstIndexGreaterorEqualToValue(sam.results.getEngineeringStrain(),
									strainValue);

						double timeValue = sam.results.time[index];
						if (radioSetBegin.isSelected()) {
							if (timeValue > 0 && timeValue < ROI.endROITime) {
								ROI.beginROITime = timeValue;
							}
						} else if (radioSetEnd.isSelected()) {
							if (timeValue < getLowestMaxTime() && timeValue > ROI.beginROITime) {
								ROI.endROITime = timeValue;
							}
						}
					} else {
						// individual sample mode
						double strainValue = (double) chart.getXAxis().getValueForDisplay(mouseEvent.getX());
						int index = 0;

						if (trueRadioButton.isSelected())
							index = SPOperations.findFirstIndexGreaterorEqualToValue(roiSample.results.getTrueStrain(),
									strainValue);
						else
							index = SPOperations.findFirstIndexGreaterorEqualToValue(
									roiSample.results.getEngineeringStrain(), strainValue);
						if (index != -1) {
							double timeValue = roiSample.results.time[index];
							if (radioSetBegin.isSelected()) {
								if (timeValue > 0 && timeValue < roiSample.getEndROITime()) {
									roiSample.setBeginROITime(timeValue);
								}
							} else if (radioSetEnd.isSelected()) {
								if (timeValue < roiSample.results.time[roiSample.results.time.length - 1]
										&& timeValue > roiSample.getBeginROITime()) {
									roiSample.setEndROITime(timeValue);
								}
							}
						}
					}
				}

				renderCharts();
			}
		});

		return chart;
	}

	private void addROIFunctionalityToTimeChart(LineChartWithMarkers<Number, Number> chart){
		if(showROIOnChart){
			chart.clearVerticalMarkers();
			Sample roiMode = roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem();
			if(roiMode == null || roiMode.placeHolderSample){
				//all samples ROI mode
				chart.addVerticalRangeMarker(new Data<Number, Number>(ROI.beginROITime * timeUnits.getMultiplier(), 
						ROI.endROITime * timeUnits.getMultiplier()), Color.GREEN);
			}
			else{
				//individual samples ROI mode
				for(Sample s : getCheckedSamples()){
					chart.addVerticalValueMarker(new Data<Number, Number>(s.getBeginROITime() * timeUnits.getMultiplier(),
							0), getSampleChartColor(s));

					chart.addVerticalValueMarker(new Data<Number, Number>(s.getEndROITime() * timeUnits.getMultiplier(),
							0), getSampleChartColor(s));

				}
			}
		}

		//set click listener
		chart.lookup(".chart-plot-background").setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				
				if (dataModifiersTitledPane.isExpanded()) {
					double timeValue = (double) chart.getXAxis().getValueForDisplay(mouseEvent.getX())
							/ timeUnits.getMultiplier();
					if (tempTrimBeginRadioButton.isSelected()) {
						for (Sample s : getCheckedSamples()) {
							DataSubset displacementData = s.getDataSubsetAtLocation(s.results.displacementDataLocation);
							DataSubset loadData = s.getDataSubsetAtLocation(s.results.loadDataLocation);
							displacementData.setBeginTempFromTimeValue(timeValue + displacementData.Data.timeData[displacementData.getBegin()]);
							loadData.setBeginTempFromTimeValue(timeValue + loadData.Data.timeData[loadData.getBegin()]);
						}
					} else if (tempTrimEndRadioButton.isSelected()) {
						for (Sample s : getCheckedSamples()) {
							DataSubset displacementData = s.getDataSubsetAtLocation(s.results.displacementDataLocation);
							DataSubset loadData = s.getDataSubsetAtLocation(s.results.loadDataLocation);
							displacementData.setEndTempFromTimeValue(timeValue + displacementData.Data.timeData[displacementData.getBegin()]);
							loadData.setEndTempFromTimeValue(timeValue + loadData.Data.timeData[loadData.getBegin()]);
						}
					}
					renderSampleResults();
					renderCharts();
				}

				else if (regionOfInterestTitledPane.isExpanded()) {
					double timeValue = (double) chart.getXAxis().getValueForDisplay(mouseEvent.getX())
							/ timeUnits.getMultiplier();
					if (radioSetBegin.isSelected()) {
						Sample roiMode = roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem();
						if (roiMode == null || roiMode.placeHolderSample) {
							if (timeValue > 0 && timeValue < ROI.endROITime) {
								ROI.beginROITime = timeValue;
							}
						} else {
							if (timeValue > 0 && timeValue < roiMode.getEndROITime())
								roiMode.setBeginROITime(timeValue);
						}
					} else if (radioSetEnd.isSelected()) {
						Sample roiMode = roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem();
						if (roiMode == null || roiMode.placeHolderSample) {
							if (timeValue < getLowestMaxTime() && timeValue > ROI.beginROITime) {
								ROI.endROITime = timeValue;
							}
						} else {
							if (timeValue < roiMode.results.time[roiMode.results.time.length - 1]
									&& timeValue > roiMode.getBeginROITime())
								roiMode.setEndROITime(timeValue);
						}
					}

					renderCharts();
				}
			}


		});
	}

	public void addXYListenerToChart(LineChartWithMarkers<Number, Number> chart){
		chart.lookup(".chart-plot-background").setOnMouseMoved(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				double xValue = (double) chart.getXAxis().getValueForDisplay(event.getX());
				double yValue = (double) chart.getYAxis().getValueForDisplay(event.getY());
				xValueLabel.setText("X: " + SPOperations.round(xValue, 4));
				yValueLabel.setText("Y: " + SPOperations.round(yValue,4));
			}

		});
	}

	public void removeSelectedSampleFromList() {
		Sample currentSelectedSample = realCurrentSamplesListView.getSelectionModel().selectedItemProperty().getValue();
		if(currentSelectedSample == null) {
			Dialogs.showInformationDialog("Error removing sample", null, "Please select sample you wish to remove",stage);
			return;
		}
		
		realCurrentSamplesListView.getItems().removeListener(sampleListChangedListener);
		
		ArrayList<Sample> keep = new ArrayList<Sample>();
		for(Sample s : realCurrentSamplesListView.getSelectionModel().getSelectedItems()){
			keep.add(s);
		}
		
		for(Sample s : keep){
			realCurrentSamplesListView.getItems().remove(s);
		}
		sampleListChangedListener.onChanged(null);
		realCurrentSamplesListView.getItems().addListener(sampleListChangedListener);
		renderROIResults();
		renderCharts();
	}



	@FXML
	public void openExportMenuButtonFired() {
		leftAccordion.setExpandedPane((TitledPane)leftAccordion.getChildrenUnmodifiable().get(0));
		xButton.setStyle("-fx-background-color: #ddd;-fx-text-fill:#FF0000;");
		HBox hBoxThatHoldsXButton = new HBox();
		hBoxThatHoldsXButton.setAlignment(Pos.CENTER_LEFT);
		hBoxThatHoldsXButton.setSpacing(15);
		hBoxThatHoldsXButton.getChildren().add(xButton);
		hBoxThatHoldsXButton.getChildren().add(new Label("Create Groups to Export"));

		VBox vbox = new VBox();
		vbox.getChildren().add(hBoxThatHoldsXButton);
		vbox.getChildren().add(tbSampleGroup);
		vbox.getChildren().add(buttonCreateSampleGroup);
		vbox.getChildren().add(treeViewSampleGroups);
		vbox.getChildren().add(buttonAddSampleToGroup);
		vbox.getChildren().add(buttonDeleteSelectedGroup);

		tbSampleGroup.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.length() > 31)
					tbSampleGroup.setText(oldValue);
			}
		});

		includeSummaryPage.setSelected(true);
		includeSummaryPage.setPadding(new Insets(10, 10, 10, 10));
		vbox.getChildren().add(includeSummaryPage);

		vbox.getChildren().add(buttonExportData);
		if(SPSettings.currentOS.contains("Mac")){
			buttonExportData.setDisable(true);
			buttonExportData.setText(buttonExportData.getText() + " (Windows Only)");
		}
		vbox.getChildren().add(buttonExportCSV);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.setSpacing(10);
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getStyleClass().add("right-vbox");
		vbox.prefHeightProperty().bind(stage.getScene().heightProperty());
		AnchorPane anchor = new AnchorPane();
		anchor.getChildren().add(vbox);
		AnchorPane.setBottomAnchor(vbox, 0.0);
		AnchorPane.setTopAnchor(vbox, 0.0);
		AnchorPane.setLeftAnchor(vbox, 0.0);
		AnchorPane.setRightAnchor(vbox, 0.0);
		if(homeSplitPane.getItems().size() > 2)
			homeSplitPane.getItems().remove(2);
		homeSplitPane.getItems().add(anchor);
		homeSplitPane.setDividerPosition(1, 1 - homeSplitPane.getDividerPositions()[0]);
	}


	private double getLowestMaxTime() {
		double minTime = Double.MAX_VALUE;
		for(Sample s : getCheckedSamples()){
			if(s.results.time[s.results.time.length-1] < minTime){
				minTime = s.results.time[s.results.time.length-1];
			}
		}
		return minTime;
	}

	private void renderROIChoiceBox(){
		String prevChoice = choiceBoxRoi.getSelectionModel().getSelectedItem();
		choiceBoxRoi.getItems().clear();
		Sample roiMode = roiSelectionModeChoiceBox.getSelectionModel().getSelectedItem();
		for(String s : displayedChartListView.getCheckModel().getCheckedItems()) {
			if(!(getCheckedSamples().size() > 1 && s.equals("Stress Vs Strain") && (roiMode == null || roiMode.placeHolderSample == true)))
				choiceBoxRoi.getItems().add(s);
		}
		
		if(choiceBoxRoi.getItems().size() == 0){
			String item = displayedChartListView.getSelectionModel().getSelectedItem();
			if(item != null && item != "")
				choiceBoxRoi.getItems().add(item);
		}
		
		if(prevChoice != null && prevChoice != "" && choiceBoxRoi.getItems().contains(prevChoice)){
			choiceBoxRoi.getSelectionModel().select(prevChoice);
		}
		else{
			if(choiceBoxRoi.getItems().size() > 0)
				choiceBoxRoi.getSelectionModel().select(0);
		}
	}

	private void renderROISelectionModeChoiceBox(){
		roiSelectionModeChoiceBox.getItems().clear();
		CompressionSample allSample = new CompressionSample();
		allSample.placeHolderSample = true;
		allSample.setName("All Samples");

		roiSelectionModeChoiceBox.getItems().add(allSample);
		for(Sample s : getCheckedSamples()){
			roiSelectionModeChoiceBox.getItems().add(s);
		}
	}

	private void fillColorList(){
		seriesColors = new ArrayList<Color>();
		for(String s : colorString)
			seriesColors.add(Color.valueOf(s));
	}

	public void showInitialOptions() {
		showSampleDirectoryButtonFired();
		leftAccordion.setExpandedPane((TitledPane)leftAccordion.getChildrenUnmodifiable().get(0));
	}

	public Color getSampleChartColor(Sample s){
		return seriesColors.get(getSampleIndex(s) % seriesColors.size());
	}

}
