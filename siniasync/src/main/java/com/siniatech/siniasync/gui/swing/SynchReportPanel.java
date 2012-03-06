package com.siniatech.siniasync.gui.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.siniatech.siniasync.change.FileContentsChange;
import com.siniatech.siniasync.change.FileMissingChange;
import com.siniatech.siniasync.change.FileTypeChange;
import com.siniatech.siniasync.change.IChange;
import com.siniatech.siniautils.fn.IResponse1;

public class SynchReportPanel extends JComponent {

    private Long noOfFilesScanned = 0L;
    private Long noOfFilesMissing = 0L;
    private Long noOfFilesChanged = 0L;
    private Long noOfFileTypeChanges = 0L;

    private JLabel noOfFilesScannedLabel;
    private JLabel noOfFilesMissingLabel;
    private JLabel noOfFilesChangedLabel;
    private JLabel noOfFileTypeChangesLabel;

    private long lastUpdate;

    private IResponse1<IChange> synchMonitor;

    public SynchReportPanel() {
        setLayout( new BorderLayout() );

        synchMonitor = createSynchMonitor();

        lastUpdate = System.currentTimeMillis();

        noOfFilesScannedLabel = new JLabel();
        noOfFilesMissingLabel = new JLabel();
        noOfFilesChangedLabel = new JLabel();
        noOfFileTypeChangesLabel = new JLabel();

        JPanel numbersPanel = new JPanel( new GridLayout( 4, 2 ) );
        numbersPanel.add( new JLabel( "Files scanned:" ) );
        numbersPanel.add( noOfFilesScannedLabel );
        numbersPanel.add( new JLabel( "Files missing:" ) );
        numbersPanel.add( noOfFilesMissingLabel );
        numbersPanel.add( new JLabel( "Files changed:" ) );
        numbersPanel.add( noOfFilesChangedLabel );
        numbersPanel.add( new JLabel( "File type changes:" ) );
        numbersPanel.add( noOfFileTypeChangesLabel );

        add( numbersPanel, BorderLayout.CENTER );
    }

    private IResponse1<IChange> createSynchMonitor() {
        return new IResponse1<IChange>() {
            @Override
            public void respond( IChange change ) {
                noOfFilesScanned++;
                if ( change instanceof FileMissingChange ) {
                    noOfFilesMissing++;
                } else if ( change instanceof FileContentsChange ) {
                    noOfFilesChanged++;
                } else if ( change instanceof FileTypeChange ) {
                    noOfFileTypeChanges++;
                }
                if ( ( System.currentTimeMillis() - lastUpdate ) > 300 ) {
                    updateView();
                }
            }
        };
    }

    private void updateView() {
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                noOfFilesScannedLabel.setText( noOfFilesScanned.toString() );
                noOfFilesMissingLabel.setText( noOfFilesMissing.toString() );
                noOfFilesChangedLabel.setText( noOfFilesChanged.toString() );
                noOfFileTypeChangesLabel.setText( noOfFileTypeChanges.toString() );
                repaint();
            }
        } );
    }

    public IResponse1<IChange> getSynchMonitor() {
        return synchMonitor;
    }

    public void startSynch() {
        noOfFilesScanned = 0L;
        noOfFilesMissing = 0L;
        noOfFilesChanged = 0L;
        noOfFileTypeChanges = 0L;
        updateView();
    }

}
