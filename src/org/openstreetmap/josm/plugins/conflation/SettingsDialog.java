// License: GPL. See LICENSE file for details. Copyright 2012 by Josh Doe and others.
package org.openstreetmap.josm.plugins.conflation;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.*;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.tools.GBC;
import static org.openstreetmap.josm.tools.I18n.tr;

/**
 * Dialog for selecting objects and configuring conflation settings
 */
public class SettingsDialog extends ExtendedDialog {
    
    private JButton freezeReferenceButton;
    private JButton freezeSubjectButton;
    private JPanel jPanel3;
    private JPanel jPanel5;
    private JButton restoreReferenceButton;
    private JButton restoreSubjectButton;
    private JLabel referenceLayerLabel;
    private JPanel referencePanel;
    private JLabel referenceSelectionLabel;
    private JLabel subjectLayerLabel;
    private JPanel subjectPanel;
    private JLabel subjectSelectionLabel;
    JCheckBox distanceCheckBox;
    JSpinner distanceWeightSpinner;
    JSpinner distanceCutoffSpinner;
    JCheckBox stringCheckBox;
    JSpinner stringWeightSpinner;
    JSpinner stringCutoffSpinner;
    JTextField stringTextField;
    
    ArrayList<OsmPrimitive> subjectSelection = null;
    ArrayList<OsmPrimitive> referenceSelection = null;
    OsmDataLayer referenceLayer;
    DataSet subjectDataSet;
    OsmDataLayer subjectLayer;
    DataSet referenceDataSet;

    public SettingsDialog() {
        super(Main.parent,
                tr("Configure conflation settings"),
                new String[]{tr("OK"), tr("Cancel")},
                false);
        initComponents();
    }

    /**
     * Build GUI components
     */
    private void initComponents() {
        referencePanel = new JPanel();
        referenceLayerLabel = new JLabel();
        referenceSelectionLabel = new JLabel();
        jPanel3 = new JPanel();
        restoreReferenceButton = new JButton(new RestoreReferenceAction());
        freezeReferenceButton = new JButton(new FreezeReferenceAction());
        subjectPanel = new JPanel();
        subjectLayerLabel = new JLabel();
        subjectSelectionLabel = new JLabel();
        jPanel5 = new JPanel();
        restoreSubjectButton = new JButton(new RestoreSubjectAction());
        freezeSubjectButton = new JButton(new FreezeSubjectAction());
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
        referencePanel.setBorder(BorderFactory.createTitledBorder(tr("Reference")));
        referencePanel.setLayout(new BoxLayout(referencePanel, BoxLayout.PAGE_AXIS));
        referenceLayerLabel.setText("(none)");
        referencePanel.add(referenceLayerLabel);
        referenceSelectionLabel.setText("Rel.:0 / Ways:0 / Nodes: 0");
        referencePanel.add(referenceSelectionLabel);
        jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.LINE_AXIS));
        restoreReferenceButton.setText(tr("Restore"));
        jPanel3.add(restoreReferenceButton);
        jPanel3.add(freezeReferenceButton);
        referencePanel.add(jPanel3);
        pnl.add(referencePanel);
        subjectPanel.setBorder(BorderFactory.createTitledBorder(tr("Subject")));
        subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.PAGE_AXIS));
        subjectLayerLabel.setText("(none)");
        subjectPanel.add(subjectLayerLabel);
        subjectSelectionLabel.setText("Rel.:0 / Ways:0 / Nodes: 0");
        subjectPanel.add(subjectSelectionLabel);
        jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.LINE_AXIS));
        restoreSubjectButton.setText(tr("Restore"));
        jPanel5.add(restoreSubjectButton);
        freezeSubjectButton.setText(tr("Freeze"));
        jPanel5.add(freezeSubjectButton);
        subjectPanel.add(jPanel5);
        pnl.add(subjectPanel);
        
        JPanel costsPanel = new JPanel();
        costsPanel.setBorder(BorderFactory.createTitledBorder(tr("Costs")));
        costsPanel.setLayout(new GridBagLayout());
        
        costsPanel.add(GBC.glue(1, 1), GBC.std());
        costsPanel.add(new JLabel(tr("Weight")), GBC.std());
        costsPanel.add(new JLabel(tr("Cutoff")), GBC.eol());
        
        distanceCheckBox = new JCheckBox();
        distanceCheckBox.setSelected(true);
        distanceCheckBox.setText(tr("Distance"));
        costsPanel.add(distanceCheckBox, GBC.std());
        distanceWeightSpinner = new JSpinner(new SpinnerNumberModel(1.0, null, null, 1.0));
        costsPanel.add(distanceWeightSpinner, GBC.std());
        distanceCutoffSpinner = new JSpinner(new SpinnerNumberModel(100.0, null, null, 1.0));
        costsPanel.add(distanceCutoffSpinner, GBC.eol());
        
        stringCheckBox = new JCheckBox();
        stringCheckBox.setSelected(false);
        stringCheckBox.setEnabled(false);
        stringCheckBox.setText(tr("String"));
        costsPanel.add(stringCheckBox, GBC.std());
        stringWeightSpinner = new JSpinner(new SpinnerNumberModel(10.0, null, null, 1.0));
        costsPanel.add(stringWeightSpinner, GBC.std());
        stringCutoffSpinner = new JSpinner(new SpinnerNumberModel(100.0, null, null, 1.0));
        costsPanel.add(stringCutoffSpinner, GBC.eol());
        stringTextField = new JTextField("name", 14);
        costsPanel.add(stringTextField, GBC.std());
        
        costsPanel.setEnabled(false);
        pnl.add(costsPanel);
        setContent(pnl);
        setupDialog();
    }

    @Override
    protected void buttonAction(int buttonIndex, ActionEvent evt) {
        super.buttonAction(buttonIndex, evt);
        if (buttonIndex == 0) {
        }
    }

    /**
     * @return the settings
     */
    public SimpleMatchSettings getSettings() {
        SimpleMatchSettings settings = new SimpleMatchSettings();
        settings.setReferenceDataSet(referenceDataSet);
        settings.setReferenceLayer(referenceLayer);
        settings.setReferenceSelection(referenceSelection);
        settings.setSubjectDataSet(subjectDataSet);
        settings.setSubjectLayer(subjectLayer);
        settings.setSubjectSelection(subjectSelection);
        
        settings.distanceCutoff = (Double)distanceCutoffSpinner.getValue();
        if (distanceCheckBox.isSelected())
            settings.distanceWeight = (Double)distanceWeightSpinner.getValue();
        else
            settings.distanceWeight = 0;
        settings.stringCutoff = (Double)stringCutoffSpinner.getValue();
        if (stringCheckBox.isSelected())
            settings.stringWeight = (Double)stringWeightSpinner.getValue();
        else
            settings.stringWeight = 0;
            
        settings.keyString = stringTextField.getText();
        
        return settings;
    }

    /**
     * @param settings the settings to set
     */
    public void setSettings(SimpleMatchSettings settings) {
        referenceDataSet = settings.getReferenceDataSet();
        referenceLayer = settings.getReferenceLayer();
        referenceSelection = settings.getReferenceSelection();
        subjectDataSet = settings.getSubjectDataSet();
        subjectLayer = settings.getSubjectLayer();
        subjectSelection = settings.getSubjectSelection();
        update();
    }

    class RestoreSubjectAction extends JosmAction {

        public RestoreSubjectAction() {
            super(tr("Restore"), null, tr("Restore subject selection"), null, false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (subjectLayer != null && subjectDataSet != null && subjectSelection != null && !subjectSelection.isEmpty()) {
                Main.map.mapView.setActiveLayer(subjectLayer);
                subjectLayer.setVisible(true);
                subjectDataSet.setSelected(subjectSelection);
            }
        }
    }

    class RestoreReferenceAction extends JosmAction {

        public RestoreReferenceAction() {
            super(tr("Restore"), null, tr("Restore reference selection"), null, false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (referenceLayer != null && referenceDataSet != null && referenceSelection != null && !referenceSelection.isEmpty()) {
                Main.map.mapView.setActiveLayer(referenceLayer);
                referenceLayer.setVisible(true);
                referenceDataSet.setSelected(referenceSelection);
            }
        }
    }

    class FreezeSubjectAction extends JosmAction {

        public FreezeSubjectAction() {
            super(tr("Freeze"), null, tr("Freeze subject selection"), null, false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (subjectDataSet != null && subjectDataSet == Main.main.getCurrentDataSet()) {
                //                subjectDataSet.removeDataSetListener(this); FIXME:
                //                subjectDataSet.removeDataSetListener(this); FIXME:
            }
            subjectDataSet = Main.main.getCurrentDataSet();
            //            subjectDataSet.addDataSetListener(tableModel); FIXME:
            //            subjectDataSet.addDataSetListener(tableModel); FIXME:
            subjectLayer = Main.main.getEditLayer();
            if (subjectDataSet == null || subjectLayer == null) {
                JOptionPane.showMessageDialog(Main.parent, tr("No valid OSM data layer present."), tr("Error freezing selection"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            subjectSelection = new ArrayList<OsmPrimitive>(subjectDataSet.getSelected());
            if (subjectSelection.isEmpty()) {
                JOptionPane.showMessageDialog(Main.parent, tr("Nothing is selected, please try again."), tr("Empty selection"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            update();
        }
    }

    class FreezeReferenceAction extends JosmAction {

        public FreezeReferenceAction() {
            super(tr("Freeze"), null, tr("Freeze subject selection"), null, false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (referenceDataSet != null && referenceDataSet == Main.main.getCurrentDataSet()) {
                //                referenceDataSet.removeDataSetListener(this); FIXME:
                //                referenceDataSet.removeDataSetListener(this); FIXME:
            }
            referenceDataSet = Main.main.getCurrentDataSet();
            //            referenceDataSet.addDataSetListener(this); FIXME:
            //            referenceDataSet.addDataSetListener(this); FIXME:
            referenceLayer = Main.main.getEditLayer();
            if (referenceDataSet == null || referenceLayer == null) {
                JOptionPane.showMessageDialog(Main.parent, tr("No valid OSM data layer present."), tr("Error freezing selection"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            referenceSelection = new ArrayList<OsmPrimitive>(referenceDataSet.getSelected());
            if (referenceSelection.isEmpty()) {
                JOptionPane.showMessageDialog(Main.parent, tr("Nothing is selected, please try again."), tr("Empty selection"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            update();
        }
    }

    /**
     * Update GUI elements
     */
    void update() {
        int numNodes = 0;
        int numWays = 0;
        int numRelations = 0;

        if (subjectSelection != null) {
            for (OsmPrimitive p : subjectSelection) {
                if (p instanceof Node) {
                    numNodes++;
                } else if (p instanceof Way) {
                    numWays++;
                } else if (p instanceof Relation) {
                    numRelations++;
                }
            }
            // FIXME: translate correctly
            subjectLayerLabel.setText(subjectLayer.getName());
            subjectSelectionLabel.setText(String.format("Rel.: %d / Ways: %d / Nodes: %d", numRelations, numWays, numNodes));
        }
        numNodes = 0;
        numWays = 0;
        numRelations = 0;
        if (referenceSelection != null) {
            for (OsmPrimitive p : referenceSelection) {
                if (p instanceof Node) {
                    numNodes++;
                } else if (p instanceof Way) {
                    numWays++;
                } else if (p instanceof Relation) {
                    numRelations++;
                }
            }

            // FIXME: translate correctly
            referenceLayerLabel.setText(referenceLayer.getName());
            referenceSelectionLabel.setText(String.format("Rel.: %d / Ways: %d / Nodes: %d", numRelations, numWays, numNodes));
        }
    }
}
