package org.millstone.examples.features;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.millstone.base.terminal.StreamResource;
import org.millstone.base.ui.Button;
import org.millstone.base.ui.Component;
import org.millstone.base.ui.Embedded;
import org.millstone.base.ui.Label;
import org.millstone.base.ui.Link;
import org.millstone.base.ui.OrderedLayout;
import org.millstone.base.ui.Panel;
import org.millstone.base.ui.Upload;
import org.millstone.base.ui.Upload.FinishedEvent;

public class FeatureFileTransfer
	extends Feature
	implements Upload.FinishedListener {

	Buffer buffer = new Buffer();

	Panel status = new Panel("Uploaded file:");
	Panel download_window = new Panel("Uploaded files:");

	

	public FeatureFileTransfer() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Upload component");

		Upload up = new Upload("Upload a file:", buffer);
		up.addListener(this);

		show.addComponent(up);
		status.setVisible(false);
		l.addComponent(status);
		l.addComponent(show);

		// Configuration
		l.addComponent(
			createPropertyPanel(
				up,
				new String[] {
					"enabled",
					"visible",
					"caption",
					"immediate",
					"description" ,
					"style"}));
		download_window.setVisible(false);
		l.addComponent(download_window);
		

		return l;
	}

	protected String getExampleSrc() {
		return "Upload u = new Upload(\"Upload a file:\", uploadReceiver);\n\n"
			+ "public class uploadReceiver \n"
			+ "implements Upload.receiver, Upload.FinishedListener { \n"
			+ "\n"
			+ " java.io.File file;\n"
			+ " java.io.FileOutputStream fos;\n"
			+ " public uploadReceiver() {\n"
			+ " }";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"Filetransfer","This demonstrates the use of the Upload component together with the Link component. "
			+ "This implementation does not actually store the file to disk, it only keeps it in a buffer. "
			+ "The example given on the example-tab on the other hand stores the file to disk and binds the link to that file.<br/>"
			+ "<br/>On the demo tab you can try out how the different properties affect the presentation of the component.","filetransfer.jpg"};
	}

	/**
	 * @see org.millstone.base.ui.Upload.FinishedListener#uploadFinished(FinishedEvent)
	 */
	public void uploadFinished(FinishedEvent event) {
		status.removeAllComponents();
		if (buffer.getStream() == null)
				status.addComponent(
					new Label("Upload finished, but output buffer is null!!"));
			else {
				status.addComponent(
					new Label(
						"<b>Name:</b> " + event.getFilename(),Label.CONTENT_XHTML));
				status.addComponent(
					new Label(
						"<b>Mimetype:</b> " + event.getMIMEType(),Label.CONTENT_XHTML));
				status.addComponent(
					new Label(
						"<b>Size:</b> "
							+ event.getLength() + " bytes.",Label.CONTENT_XHTML)
							);

				Link l = new Link(buffer.getFileName(),new StreamResource(buffer,buffer.getFileName(),this.getApplication()));
				download_window.addComponent(l);
				download_window.setVisible(true);
				status.setVisible(true);						  
			}
	}

	public class Buffer implements StreamResource.StreamSource, Upload.Receiver {
		ByteArrayOutputStream outputBuffer = null;
		String mimeType;
		String fileName;
		
		public Buffer() {
			
		}		
		/**
		 * @see org.millstone.base.terminal.StreamResource.StreamSource#getStream()
		 */
		public InputStream getStream() {
			if (outputBuffer == null) return null;
			return new ByteArrayInputStream(outputBuffer.toByteArray());
		}

		/**
		 * @see org.millstone.base.ui.Upload.Receiver#receiveUpload(String, String)
		 */
		public OutputStream receiveUpload(String filename, String MIMEType) {
			fileName = filename;
			mimeType = MIMEType;
			outputBuffer = new ByteArrayOutputStream();
			return outputBuffer;
		}

		/**
		 * Returns the fileName.
		 * @return String
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * Returns the mimeType.
		 * @return String
		 */
		public String getMimeType() {
			return mimeType;
		}

	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */