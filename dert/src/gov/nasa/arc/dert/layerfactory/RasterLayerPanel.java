/**

DERT is a viewer for digital terrain models created from data collected during NASA missions.

DERT is Released in under the NASA Open Source Agreement (NOSA) found in the “LICENSE” folder where you
downloaded DERT.

DERT includes 3rd Party software. The complete copyright notice listing for DERT is:

Copyright © 2015 United States Government as represented by the Administrator of the National Aeronautics and
Space Administration.  No copyright is claimed in the United States under Title 17, U.S.Code. All Other Rights
Reserved.

Desktop Exploration of Remote Terrain (DERT) could not have been written without the aid of a number of free,
open source libraries. These libraries and their notices are listed below. Find the complete third party license
listings in the separate “DERT Third Party Licenses” pdf document found where you downloaded DERT in the
LICENSE folder.
 
JogAmp Ardor3D Continuation
Copyright © 2008-2012 Ardor Labs, Inc.
 
JogAmp
Copyright 2010 JogAmp Community. All rights reserved.
 
JOGL Portions Sun Microsystems
Copyright © 2003-2009 Sun Microsystems, Inc. All Rights Reserved.
 
JOGL Portions Silicon Graphics
Copyright © 1991-2000 Silicon Graphics, Inc.
 
Light Weight Java Gaming Library Project (LWJGL)
Copyright © 2002-2004 LWJGL Project All rights reserved.
 
Tile Rendering Library - Brain Paul 
Copyright © 1997-2005 Brian Paul. All Rights Reserved.
 
OpenKODE, EGL, OpenGL , OpenGL ES1 & ES2
Copyright © 2007-2010 The Khronos Group Inc.
 
Cg
Copyright © 2002, NVIDIA Corporation
 
Typecast - David Schweinsberg 
Copyright © 1999-2003 The Apache Software Foundation. All rights reserved.
 
PNGJ - Herman J. Gonzalez and Shawn Hartsock
Copyright © 2004 The Apache Software Foundation. All rights reserved.
 
Apache Harmony - Open Source Java SE
Copyright © 2006, 2010 The Apache Software Foundation.
 
Guava
Copyright © 2010 The Guava Authors
 
GlueGen Portions
Copyright © 2010 JogAmp Community. All rights reserved.
 
GlueGen Portions - Sun Microsystems
Copyright © 2003-2005 Sun Microsystems, Inc. All Rights Reserved.
 
SPICE
Copyright © 2003, California Institute of Technology.
U.S. Government sponsorship acknowledged.
 
LibTIFF
Copyright © 1988-1997 Sam Leffler
Copyright © 1991-1997 Silicon Graphics, Inc.
 
PROJ.4
Copyright © 2000, Frank Warmerdam

LibJPEG - Independent JPEG Group
Copyright © 1991-2018, Thomas G. Lane, Guido Vollbeding
 

Disclaimers

No Warranty: THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY KIND,
EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
THAT THE SUBJECT SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY
WARRANTY THAT THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE. THIS AGREEMENT
DOES NOT, IN ANY MANNER, CONSTITUTE AN ENDORSEMENT BY GOVERNMENT AGENCY OR ANY
PRIOR RECIPIENT OF ANY RESULTS, RESULTING DESIGNS, HARDWARE, SOFTWARE PRODUCTS OR
ANY OTHER APPLICATIONS RESULTING FROM USE OF THE SUBJECT SOFTWARE.  FURTHER,
GOVERNMENT AGENCY DISCLAIMS ALL WARRANTIES AND LIABILITIES REGARDING THIRD-PARTY
SOFTWARE, IF PRESENT IN THE ORIGINAL SOFTWARE, AND DISTRIBUTES IT "AS IS."

Waiver and Indemnity:  RECIPIENT AGREES TO WAIVE ANY AND ALL CLAIMS AGAINST THE UNITED
STATES GOVERNMENT, ITS CONTRACTORS AND SUBCONTRACTORS, AS WELL AS ANY PRIOR
RECIPIENT.  IF RECIPIENT'S USE OF THE SUBJECT SOFTWARE RESULTS IN ANY LIABILITIES,
DEMANDS, DAMAGES, EXPENSES OR LOSSES ARISING FROM SUCH USE, INCLUDING ANY DAMAGES
FROM PRODUCTS BASED ON, OR RESULTING FROM, RECIPIENT'S USE OF THE SUBJECT SOFTWARE,
RECIPIENT SHALL INDEMNIFY AND HOLD HARMLESS THE UNITED STATES GOVERNMENT, ITS
CONTRACTORS AND SUBCONTRACTORS, AS WELL AS ANY PRIOR RECIPIENT, TO THE EXTENT
PERMITTED BY LAW.  RECIPIENT'S SOLE REMEDY FOR ANY SUCH MATTER SHALL BE THE IMMEDIATE,
UNILATERAL TERMINATION OF THIS AGREEMENT.

**/

package gov.nasa.arc.dert.layerfactory;

import gov.nasa.arc.dert.landscape.layer.LayerInfo.LayerType;
import gov.nasa.arc.dert.raster.RasterFile;
import gov.nasa.arc.dert.raster.geotiff.GTIF;
import gov.nasa.arc.dert.raster.pds.PDS;
import gov.nasa.arc.dert.ui.GBCHelper;
import gov.nasa.arc.dert.ui.LandscapeChooserDialog;
import gov.nasa.arc.dert.util.FileHelper;
import gov.nasa.arc.dert.util.StringUtil;
import gov.nasa.arc.dert.util.UIUtil;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Provides a panel to create a multi-resolution tiled pyramid from a raster
 * file in PDS or GeoTIFF (including BigTIFF) format.
 *
 */
public class RasterLayerPanel extends JPanel {

	// Missing value controls
	private JRadioButton useMetadataButton;
	private JTextField equalToText;
	private JRadioButton equalToButton;

	// Margin controls
	private JCheckBox autoMargin;
	private JPanel marginPanel;
	private JTextField topEdge, bottomEdge, leftEdge, rightEdge;

	// Text fields
	private JTextField fileText, landscapeText, messageText, nameText;

	private JLabel nameLabel;

	// Combo boxes
	private JComboBox tileSizeMenu;
	private JComboBox typeMenu;
	private JComboBox globeMenu;

	// Factory for creating the pyramid
	private RasterPyramidLayerFactory factory;

	// Properties
	private Properties dertProperties;
	
	// Last directory visited
	private String lastPath;

	// Command line arguments
	private String landscapePath;
	private String filePath;
	private String missing;
	private int tileSize;
	private LayerType layerType;
	private String layerName;
	private String globe;
	private int[] margin;

	/**
	 * Create the dialog
	 * 
	 * @param args
	 *            command line arguments
	 */
	public RasterLayerPanel(JTextField mText, Properties properties, String lPath, String fPath, String miss,
		int tSize, LayerType lType, String lName, String glb, int[] mrg) {
		messageText = mText;
		dertProperties = properties;
		landscapePath = lPath;
		filePath = fPath;
		missing = miss;
		tileSize = tSize;
		layerType = lType;
		layerName = lName;
		globe = glb;
		margin = mrg;

		JPanel container = this;

		GridBagLayout gridLayout = new GridBagLayout();
		container.setLayout(gridLayout);

		JLabel label = new JLabel("Input File:");
		label.setToolTipText("enter GeoTIFF or PDS file");
		container.add(label, GBCHelper.getGBC(0, 0, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 0, 0));

		fileText = new JTextField();
		if (filePath != null) {
			fileText.setText(filePath);
		}
		container.add(fileText, GBCHelper.getGBC(1, 0, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0));

		JButton button = new JButton("Browse");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setInputFile();
			}
		});
		container.add(button, GBCHelper.getGBC(4, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 0, 0));

		label = new JLabel("Layer Type:");
		label.setToolTipText("select the type of landscape layer to be created");
		container.add(label, GBCHelper.getGBC(0, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 0, 0));

		typeMenu = new JComboBox(LayerFactory.LAYER_TYPE);
		if (layerType != null) {
			typeMenu.setSelectedItem(layerType);
		} else {
			typeMenu.setSelectedItem(LayerType.elevation);
		}
		typeMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				updateNameText();
			}
		});
		container.add(typeMenu, GBCHelper.getGBC(1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 0, 0));

		label = new JLabel("Globe:", SwingConstants.RIGHT);
		label.setToolTipText("select the planet or moon");
		container.add(label, GBCHelper.getGBC(2, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 0, 0));

		globeMenu = new JComboBox(LayerFactory.GLOBE_NAME);
		globeMenu.setSelectedIndex(0);
		container.add(globeMenu, GBCHelper.getGBC(3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 0, 0));

		label = new JLabel("Missing value:");
		label.setToolTipText("indicate the missing value for the input file");
		container.add(label, GBCHelper.getGBC(0, 2, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 0, 0));

		JPanel missingComp = new JPanel();
		missingComp.setLayout(new GridLayout(1, 4));
		container.add(missingComp, GBCHelper.getGBC(1, 2, 4, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0));
		ButtonGroup group = new ButtonGroup();

		useMetadataButton = new JRadioButton("file metadata/NaN");
		useMetadataButton.setHorizontalAlignment(SwingConstants.RIGHT);
		useMetadataButton.setSelected(true);
		missingComp.add(useMetadataButton);
		group.add(useMetadataButton);

		equalToButton = new JRadioButton("equal to:");
		equalToButton.setHorizontalAlignment(SwingConstants.RIGHT);
		missingComp.add(equalToButton);
		group.add(equalToButton);

		equalToText = new JTextField();
		missingComp.add(equalToText);
		equalToText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent event) {
				equalToButton.setSelected(true);
				useMetadataButton.setSelected(false);
			}

			@Override
			public void focusLost(FocusEvent event) {
			}
		});

		// Pyramid directory

		label = new JLabel("Landscape: ");
		container.add(label, GBCHelper.getGBC(0, 3, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 0, 0));
		label.setToolTipText("enter the output landscape directory or create a new one");

		landscapeText = new JTextField();
		if (landscapePath != null) {
			landscapeText.setText(landscapePath);
		}
		container.add(landscapeText, GBCHelper.getGBC(1, 3, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0));

		button = new JButton("Browse");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setLandscapeText();
			}
		});
		container.add(button, GBCHelper.getGBC(4, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 0, 0));

		// Layer name

		nameLabel = new JLabel("Layer Name: ");
		container.add(nameLabel, GBCHelper.getGBC(0, 4, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 0, 0));
		nameLabel.setToolTipText("enter the output landscape layer name");

		nameText = new JTextField();
		updateNameText();
		if (typeMenu.getSelectedItem() == LayerType.elevation) {
			nameText.setEnabled(false);
			nameLabel.setForeground(Color.gray);
			nameText.setText("elevation");
		} else if (layerName != null) {
			nameText.setText(layerName);
		}
		container.add(nameText, GBCHelper.getGBC(1, 4, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0));

		label = new JLabel("Tile Size: ", SwingConstants.RIGHT);
		label.setToolTipText("select a tile size");
		container.add(label, GBCHelper.getGBC(3, 4, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 0, 0));

		tileSizeMenu = new JComboBox(LayerFactory.TILE_SIZE);
		tileSizeMenu.setEditable(false);
		tileSizeMenu.setSelectedIndex(4);
		container.add(tileSizeMenu, GBCHelper.getGBC(4, 4, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 0, 0));

		autoMargin = new JCheckBox("automatically add margins");
		autoMargin.setSelected(true);
		autoMargin.setToolTipText("automatically extend raster with missing value pixels to make each side a power of 2");
		autoMargin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				UIUtil.setEnabled(marginPanel, !autoMargin.isSelected());
			}
		});
		container.add(autoMargin, GBCHelper.getGBC(1, 5, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0));

		label = new JLabel("Margin (pixels):");
		label.setToolTipText("number of missing value pixels to use to extend the side (include extra for bilinear interpolation at edges)");
		container.add(label, GBCHelper.getGBC(0, 5, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 0, 0));
		marginPanel = new JPanel();
		marginPanel.setLayout(new GridLayout(1, 8));
		marginPanel.add(new JLabel("Left", SwingConstants.RIGHT));
		leftEdge = new JTextField();
		leftEdge.setText("0");
		marginPanel.add(leftEdge);
		marginPanel.add(new JLabel("Right", SwingConstants.RIGHT));
		rightEdge = new JTextField();
		rightEdge.setText("0");
		marginPanel.add(rightEdge);
		marginPanel.add(new JLabel("Top", SwingConstants.RIGHT));
		topEdge = new JTextField();
		topEdge.setText("0");
		marginPanel.add(topEdge);
		marginPanel.add(new JLabel("Bottom", SwingConstants.RIGHT));
		bottomEdge = new JTextField();
		bottomEdge.setText("0");
		marginPanel.add(bottomEdge);
		UIUtil.setEnabled(marginPanel, false);
		container.add(marginPanel, GBCHelper.getGBC(0, 6, 5, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0));
	}

	/**
	 * Apply button was pressed
	 */
	public boolean applyPressed() {
		landscapePath = landscapeText.getText().trim();
		if (landscapePath.length() == 0) {
			messageText.setText("Please select a landscape.");
			return (false);
		}
		String filetxt = fileText.getText().trim();
		if ((filetxt == null) || (filetxt.length() == 0)) {
			messageText.setText("Please select an input file.");
			return (false);
		}
		String str = null;
		String missingValue = null;
		if (equalToButton.isSelected()) {
			str = equalToText.getText();
			if (!str.isEmpty()) {
				try {
					Float.valueOf(str);
					missingValue = str;
				} catch (Exception e) {
					messageText.setText("Invalid entry for missing value.");
					return (false);
				}
			}
		}
		str = (String) tileSizeMenu.getSelectedItem();
		tileSize = Integer.parseInt(str);
		layerType = (LayerType) typeMenu.getSelectedItem();
		if (layerType == LayerType.elevation) {
			layerName = "elevation";
		} else {
			layerName = nameText.getText().trim();
			if (layerName.isEmpty()) {
				messageText.setText("Please enter a name for the layer.");
				return (false);
			}
		}
		globe = (String) globeMenu.getSelectedItem();
		if (globe.equals("Use Metadata")) {
			globe = null;
		}
		if (!autoMargin.isSelected()) {
			try {
				margin = new int[4];
				String s = leftEdge.getText();
				margin[0] = Integer.parseInt(s);
				s = rightEdge.getText();
				margin[1] = Integer.parseInt(s);
				s = bottomEdge.getText();
				margin[2] = Integer.parseInt(s);
				s = topEdge.getText();
				margin[3] = Integer.parseInt(s);
			} catch (Exception e) {
				messageText.setText("Invalid margin value.");
				return (false);
			}
		}
		missing = missingValue;
		if (landscapePath != null) {
			messageText.setText("Creating pyramid . . .");
			String fPath = filetxt.toLowerCase();
			RasterFile rf = null;
			if (fPath.endsWith(".img") || fPath.endsWith(".lbl")) {
				rf = new PDS(filetxt, dertProperties);
			} else if (fPath.endsWith(".tiff") || fPath.endsWith(".tif") || fPath.endsWith(".gtif")
				|| fPath.endsWith(".gtiff")) {
				rf = new GTIF(filetxt, dertProperties);
			} else {
				messageText.setText("Only NASA PDS and GeoTIFF formats are supported.");
				return (false);
			}

			// Get optional temporary file path
			String tmpPath = dertProperties.getProperty("LayerTemporaryPath", null);
			if ((tmpPath != null) && tmpPath.startsWith("$"))
				tmpPath = System.getProperty(tmpPath.substring(1));
			
			factory = new RasterPyramidLayerFactory(rf, tmpPath);
			return (true);
		}
		return (false);
	}

	public boolean run() {
		try {
			factory.buildPyramid(landscapePath, globe, layerType, layerName, tileSize, missing, margin, messageText);
			return (true);
		} catch (Exception e) {
			messageText.setText("Unable to complete pyramid.");
			e.printStackTrace();
			factory = null;
			return (false);
		}

	}

	/**
	 * Cancel button was pressed
	 */
	public boolean cancelPressed() {
		if (factory != null) {
			factory.cancel();
			factory = null;
			return (false);
		} else {
			return (true);
		}
	}

	/**
	 * Get the input raster file.
	 */
	protected void setInputFile() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("img,lbl,tif,tiff,gtif,gtiff", "img", "lbl", "tif", "tiff",
			"gtif", "gtiff");
		String fPath = FileHelper.getFilePathForOpen("Input File Selection", filter);
		if (fPath != null) {
			fileText.setText(fPath);
			updateNameText();
			lastPath = FileHelper.getLastFilePath();
		}
	}

	/**
	 * Get the destination landscape directory.
	 */
	protected void setLandscapeText() {
		LandscapeChooserDialog chooser = new LandscapeChooserDialog(lastPath);
		chooser.open();
		String landscapePath = chooser.getLandscape();
		if (landscapePath != null) {
			landscapeText.setText(landscapePath);
			lastPath = chooser.getLastFilePath();
		}
	}

	private void updateNameText() {
		if (typeMenu.getSelectedItem() == LayerType.elevation) {
			nameText.setEnabled(false);
			nameLabel.setForeground(Color.gray);
			nameText.setText("elevation");
		} else {
			nameText.setEnabled(true);
			nameLabel.setForeground(Color.black);
			String str = fileText.getText();
			if ((str != null) && !str.isEmpty()) {
				nameText.setText(StringUtil.getLabelFromFilePath(str));
			}
		}
	}

}
