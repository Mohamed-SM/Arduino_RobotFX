package robotFX.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;
import robotFX.Main;
import robotFX.utiles.OpenCVUtils;
import robotFX.utiles.Utile;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired video stream, the relative
 * controls and the image segmentation process.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 2.0 (2017-03-10)
 * @since 1.0 (2015-01-13)
 * 
 */
public class ObjRecognitionController
{
	// FXML camera button
	@FXML
	private Button cameraButton;
	// the FXML area for showing the current frame
	@FXML
	private ImageView originalFrame;

	// the FXML area for showing the mask
	@FXML
	private ImageView maskImage;
	// the FXML area for showing the output of the morphological operations
	@FXML
	private ImageView morphImage;
	// FXML slider for setting HSV ranges
	@FXML
	private Slider hueStart;
	@FXML
	private Slider hueStop;
	@FXML
	private Slider saturationStart;
	@FXML
	private Slider saturationStop;
	@FXML
	private Slider valueStart;
	@FXML
	private Slider valueStop;
	@FXML
    private TableView<Point> pointTable;
	@FXML
    private TableColumn<Point,String> pointTableX;
    @FXML
    private TableColumn<Point,String> pointTableY;
	
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that performs the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
	private boolean cameraActive;
	
	// property for object binding
	private ObjectProperty<String> hsvValuesProp;
    private Main main;
    private ObservableList<Point> points;
	private ObservableList<Point> AnchorPoints = FXCollections.observableArrayList();
    private int counter = 0;

    @FXML
    private void initialize(){
        originalFrame.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ImageView fulloriginalFrame = new ImageView();
                fulloriginalFrame.imageProperty().bind(originalFrame.imageProperty());
                this.main.fullScreenCam(fulloriginalFrame);
            }else {
                AnchorPoints.add(new Point(
                        Utile.map(event.getX(),0,originalFrame.getFitHeight(),0,480),
                        Utile.map(event.getY(),0,originalFrame.getFitWidth(),0,640)
                ));
                if(AnchorPoints.size()>4){
                    AnchorPoints.remove(0);
                }
            }
        });

        //maping the video resolution 640*480 to real dimentions in cm 45*34
        // the Screen Y is the Real X Axe so the axes are inversed
        //remove from 34 becous the X is in the top but i real it's in the bottom
        pointTableX.setCellValueFactory(cellData ->
                {
                    if(AnchorPoints.size()<3) return null;
                    return new SimpleStringProperty(""
                            + (29 - Utile.map(cellData.getValue().y, AnchorPoints.get(0).y, AnchorPoints.get(1).y, 0, 29))
                    );
                }
        );

        //remove 22.5 to make the center 0 and not 22.5
        pointTableY.setCellValueFactory(cellData ->
                {
                    if(AnchorPoints.size()<3) return null;
                    return new SimpleStringProperty("" +
                            (Utile.map(cellData.getValue().x, AnchorPoints.get(0).x, AnchorPoints.get(2).x, 0, 40) - 20)
                    );
                }
        );


        //TODO get table selection
        /*pointTable.setRowFactory(tv -> {
            TableRow<Point> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    int index = pointTable.getSelectionModel().getSelectedIndex();
                    System.out.println(index);
                    Point point = points.get(index);
                    Point point1 = new Point(
                            (29 - Utile.map(point.y,AnchorPoints.get(0).y,AnchorPoints.get(1).y,0,29)),
                            (Utile.map(point.x,AnchorPoints.get(0).x,AnchorPoints.get(2).x,0,40) - 20)
                    );
                    System.out.println(point);
        			main.grap(point);
                }
            });
            return row;
        });*/
    }

    public void setMainApp(Main main,ObservableList<Point> points) {
        this.main = main;
        this.points = points;
        pointTable.setItems(this.points);
    }


	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	private void startCamera()
	{
		// bind a text property with the string containing the current range of
		// HSV values for object detection
		hsvValuesProp = new SimpleObjectProperty<>();

		// set a fixed width for all the image to show and preserve image ratio
		this.imageViewProperties(this.originalFrame, 800);
		this.imageViewProperties(this.maskImage, 200);
		this.imageViewProperties(this.morphImage, 200);
		
		if (!this.cameraActive)
		{
			// start the video capture
			this.capture.open(1);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{

				this.cameraActive = true;

				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = () -> {
                    // effectively grab and process a single frame
                    Mat frame = grabFrame();
                    // convert and show the frame
                    Image imageToShow = OpenCVUtils.mat2Image(frame);
                    updateImageView(originalFrame, imageToShow);

                    if (this.timer.isShutdown()) {
                        //set the placeholder image back

                        try {
                            Image image = new Image(this.getClass().getResource("images/camera-logo.jpg").toURI().toString());
                            updateImageView(originalFrame,image);
                            updateImageView(maskImage,image);
                            updateImageView(morphImage,image);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                };

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				// update the button content
				this.cameraButton.setText("Stop Camera");
			}
			else
			{
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.cameraButton.setText("Start Camera");

            // stop the timer
            this.stopAcquisition();

            //set the placeholder image back
            Image image;
            try {
                image = new Image(this.getClass().getResource("images/camera-logo.jpg").toURI().toString());
                updateImageView(originalFrame,image);
                updateImageView(maskImage,image);
                updateImageView(morphImage,image);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
		}
	}

	/**
	 * Get a frame from the opened video stream (if any)
	 *
	 * @return the {@link Image} to show
	 */
	private Mat grabFrame()
	{
		Mat frame = new Mat();

		// check if the capture is open
		if (this.capture.isOpened())
			try {
				// read the current frame
				this.capture.read(frame);

				// if the frame is not empty, process it
				if (!frame.empty()) {
					// init
					Mat blurredImage = new Mat();
					Mat hsvImage = new Mat();
					Mat mask = new Mat();
					Mat morphOutput = new Mat();

					// remove some noise
					Imgproc.blur(frame, blurredImage, new Size(7, 7));


					// convert the frame to HSV
					Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

					// get thresholding values from the UI
					// remember: H ranges 0-180, S and V range 0-255
					Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
							this.valueStart.getValue());
					Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
							this.valueStop.getValue());

					// show the current selected HSV range
					String valuesToPrint = "Hue range: " + minValues.val[0] + " - " + maxValues.val[0]
							+ " Saturation range: " + minValues.val[1] + " - " + maxValues.val[1]
							+ " Value range: " + minValues.val[2] + " - " + maxValues.val[2];
					OpenCVUtils.onFXThread(this.hsvValuesProp, valuesToPrint);

					// threshold HSV image to select tennis balls
					Core.inRange(hsvImage, minValues, maxValues, mask);
					// show the partial output
					this.updateImageView(this.maskImage, OpenCVUtils.mat2Image(mask));

					// morphological operators
					// dilate with large element, erode with small ones
					Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
					Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

					Imgproc.erode(mask, morphOutput, erodeElement);
					Imgproc.erode(morphOutput, morphOutput, erodeElement);

					Imgproc.dilate(morphOutput, morphOutput, dilateElement);
					Imgproc.dilate(morphOutput, morphOutput, dilateElement);

					// show the partial output
					this.updateImageView(this.morphImage, OpenCVUtils.mat2Image(morphOutput));

					// find the tennis ball(s) contours and show them
					frame = this.findAndDrawBalls(morphOutput, frame);

					//Draw the anchors
					for (Point AnchorPoint : AnchorPoints) {
						Imgproc.circle(
								frame,                 //Matrix obj of the image
								AnchorPoint,    //Center of the circle
								1,                    //Radius
								new Scalar(0, 255, 0),  //Scalar object for color
								5                      //Thickness of the circle
						);
					}

				}

			} catch (Exception e) {
				// log the (full) error
				System.err.print("Exception during the image elaboration...");
				e.printStackTrace();
			}

		return frame;
	}

	/**
	 * Given a binary image containing one or more closed surfaces, use it as a
	 * mask to find and highlight the objects contours
	 *
	 * @param maskedImage
	 *            the binary image to be used as a mask
	 * @param frame
	 *            the original {@link Mat} image to be used for drawing the
	 *            objects contours
	 * @return the {@link Mat} image with the objects contours framed
	 */
	private Mat findAndDrawBalls(Mat maskedImage, Mat frame)
	{
        counter++;
		// init
		List<MatOfPoint> contours = new ArrayList<>();

		Mat hierarchy = new Mat();

		// find contours
		Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		// if any contour exist...
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
		{
			// for each contour, display it in blue
			for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
			{
				Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));

				//Clear the list before filling it again
                if (counter > 10) {
                    points.clear();
                }

                //make a list of point of the center of detected objects
				for(MatOfPoint contour : contours) {

					MatOfPoint mop = new MatOfPoint();
					mop.fromList(contour.toList());


					Moments moments = Imgproc.moments(mop);

					Point centroid = new Point();

					centroid.x = moments.get_m10() / moments.get_m00();
					centroid.y = moments.get_m01() / moments.get_m00();

                    if (counter > 10) {
                        points.add(centroid);
                    }

                    Imgproc.circle (
					         frame,                 //Matrix obj of the image
					         centroid,    //Center of the circle
					         1,                    //Radius
					         new Scalar(0, 0, 255),  //Scalar object for color
					         5                      //Thickness of the circle
					      );

				}

			}
		}

        //reset the counter
        if (counter > 10) {
            counter = 0;
        }

		return frame;

	}

	/**
	 * Set typical {@link ImageView} properties: a fixed width and the
	 * information to preserve the original image ration
	 *
	 * @param image
	 *            the {@link ImageView} to use
	 * @param dimension
	 *            the width of the image to set
	 */
	private void imageViewProperties(ImageView image, int dimension)
	{
		// set a fixed width for the given ImageView
		image.setFitWidth(dimension);
		// preserve the image ratio
		image.setPreserveRatio(true);
	}

	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition()
	{
		if (this.timer!=null && !this.timer.isShutdown())
		{
			try
			{
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		

		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}
	
	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image)
	{
		OpenCVUtils.onFXThread(view.imageProperty(), image);
	}
	
	/**
	 * On application close, stop the acquisition from the camera
	 */
	public void setClosed()
	{
		this.stopAcquisition();
	}

    @FXML
    public void grapAll(){
	    ObservableList<Point> courentPointList = FXCollections.observableArrayList();
	    points.forEach(point -> {
            Point point1 = new Point(
                    (29 - Utile.map(point.y,AnchorPoints.get(0).y,AnchorPoints.get(1).y,0,29)),
                    (Utile.map(point.x,AnchorPoints.get(0).x,AnchorPoints.get(2).x,0,40) - 20)
            );
            courentPointList.add(point1);
        });

	    this.main.grapAll(courentPointList);
    }

}
