package benLaw;

 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage; 
 
public class MainClass extends Application {
private String[] number = {"1","2","3","4","5","6","7","8","9"}	; 
private static double[] benValues =null;
private  static double[] benPercent= null;
private static String[] dataSet = {"1","2","3","4","5","6","7","8","9"};
private static int[] dataOccur = {0,0,0,0,0,0,0,0,0};
private static double[] occurValues =null;;
private static double[] occurPercent = new double[9];
private static double[] diff = new double[9];
private static String path="";
private static int size;

private static double sumDev;
 
public  static void main( String a[])  {
	Application.launch(a);
}
public static final String PATH_MAIN ="css/sky.css";
private static String[] load (String p) throws IOException {
	FileInputStream in = new FileInputStream(new File(p));
	 String main ="";
	 String lines[]; 
	 int i=in.read();
	 while(i!=-1) {
		main+=(char)i;
		i=in.read();
	 }
	 lines=new String[main.split("\n").length];
	 List<String> res = new ArrayList<String>();		 
	for(String line:main.split("\n")) {
		 		for(String value:line.split(",") ) {
		 			try {
		 			Double.parseDouble(value);
		 			res.add(value);
		 			}catch(Exception e) {
		 				
		 			}
		 			
		
		}
	}
	System.err.println("Number rows "+main.split("\n").length);
	
	
	
	return (String[]) res.toArray(new String[0]) ;
}

BorderPane main;
@Override
public void start(Stage myStage) throws Exception {
	

	 
		// Give the stage a title.
		myStage.setTitle("Benford's Law:  Fraud Detection program by Ochohe Oode Margaret");
		main = new BorderPane();
		Label status = new Label("Select a File");
		 
		Button loadFile = new Button("Load CSV File");
		Button plotGraph = new Button("Plot Chart"); 
		TextArea resultView= new TextArea();
		loadFile.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser f= new FileChooser();
				 
				File file=f.showOpenDialog(myStage);
				 if(file!=null) {
					 path=file.getAbsolutePath();
					 status.setText("Selected File "+path);
				 }else {
					 status.setText("File not Selected ");
				 }
			}

		});
		plotGraph.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
	
				File file =new File(path);
				if(file!=null)
  if(file.isFile()) {
	String title ="Plot of Benford's Law On "+file.getName();
	if(callBenford()) {
		Stage stage = new Stage();
		stage.setTitle(title);
		BorderPane main = new BorderPane();
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Frequency");
		 		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Percentage");
		BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle("Fraud Detection Plot");
		// Set the data for the chart
		 String[] barName ={"Benford",file.getName()};
		ObservableList<XYChart.Series<String,Number>> chartData =
				getPlot(barName, benPercent,occurPercent);
		 
		chart.setData(chartData);
		StackPane root = new StackPane(chart);
		main.setBottom(new Label("Deviation::  "+ new Double(sumDev).toString()+"%"));
		ScrollPane sp = new ScrollPane(root);
		resultView.setText(result);
		main.setCenter(sp);
		Scene scene = new Scene(main,900,600);
		scene.getStylesheets().add(PATH_MAIN); 
		stage.setScene(scene);
		stage.show();
	}else {
		status.setText("File is corrupt or deleted");	
	}
}else {
	status.setText("File not Selected");
}
				
			}

		});
		 
 
		FlowPane flow = new FlowPane(loadFile, plotGraph);
		main.setTop(flow);
		main.setCenter(resultView);
		main.setBottom(status);
		 
		Scene myScene = new Scene(main, 800, 500);
		// Set the scene on the stage.
		myScene.getStylesheets().add(PATH_MAIN); 
		myStage.setScene(myScene);
		// Show the stage and its scene.
		myStage.show(); 
					//Platform.runLater(() -> status.setText("Database is Print")); 

}
static String result="";
public static boolean  callBenford() {

	try {
		dataSet=load(path);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		System.err.println("Unable to Open .......................");
		
		e.printStackTrace();
		return false; 
	}

  benValues= new double[9];
  benPercent= new double[9]; 
  dataOccur = new int[9];
 occurValues = new double[9];
  occurPercent = new double[9];
 diff = new double[9];
	size=dataSet.length;
	for(int i = 0; i<9;i++) {
		int k=i+1;
		benValues[i] =Math.log10(1+((double)1/(double)k))/Math.log10(10);
		benPercent[i] =benValues[i]*100;
	}
	char ch='0';
	for(String value:dataSet) {
		if(value.length()>0)
		ch =value.charAt(0);
		else 
			ch='e';
		switch(ch) {
		case '1' : dataOccur[0]++;break;
		case '2' : dataOccur[1]++;break;
		case '3' : dataOccur[2]++;break;
		case '4' : dataOccur[3]++;break;
		case '5' : dataOccur[4]++;break;
		case '6' : dataOccur[5]++;break;
		case '7' : dataOccur[6]++;break;
		case '8' : dataOccur[7]++;break;
		case '9' : dataOccur[8]++;break;
        default:  
		}
	}
	System.out.println();
	sumDev=0;
	String ir="|Number|	|Benford Values|  |Benford %Value|    |Data Ocurr|    |ocurr Value|    |percent Ocurr|	|ABsS Deviation|\n";
	DecimalFormat d = new DecimalFormat("#.00");
	for(int i = 0; i<9;i++) {
		 
 occurValues[i]=(double)dataOccur[i]/(double)size;
occurPercent[i]=occurValues[i]*100;	
diff[i]=Math.abs(benPercent[i]-occurPercent[i]);
sumDev+=diff[i];

result+=ir;

System.out.print(ir);
ir= new Integer(i+1).toString()+"\t\t\t" +new Double(d.format( benValues[i])).toString()+"\t\t\t"
+new Double(d.format(benPercent[i])).toString()+"\t\t\t	"
		+new Integer(dataOccur[i]).toString()+"\t\t\t"
		+"     "+new Double(d.format(occurValues[i])).toString()+"\t\t\t"+
		new Double(d.format(occurPercent[i])).toString()+"\t\t\t"+
		new Double(d.format(diff[i])).toString()+"\n";


	}
	
	System.out.println("Deviation "+ (d.format(sumDev) ));
	return true;
}


public static ObservableList<XYChart.Series<String, Number>> getPlot(String[]names,double[]... data ) {
	/*
	 * String... strings) {
                if(strings[0]=="comfirm") {

	 */
	List<XYChart.Series<String, Number>> series =new ArrayList<XYChart.Series<String, Number>>();
	int count=0;
	for(double[] d:data) {
		XYChart.Series<String, Number> ser= new XYChart.Series<>();
		
		if(names[count]!=null)
		ser.setName(names[count]);
		else
			ser.setName(new Integer(count).toString());
		count++;
		int i =0;
		for(double val:d) {
			i++;
			ser.getData().addAll(new XYChart.Data<>(new Integer(i).toString(), val));
			
		}
		series.add(ser);
	}

ObservableList<XYChart.Series<String, Number>> result =
FXCollections.<XYChart.Series<String, Number>>observableArrayList();
result.addAll(series);
return  result;
}

}
