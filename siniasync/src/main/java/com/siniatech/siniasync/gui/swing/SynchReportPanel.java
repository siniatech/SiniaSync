package com.siniatech.siniasync.gui.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.siniatech.siniasync.change.FileContentsChange;
import com.siniatech.siniasync.change.FileMissingChange;
import com.siniatech.siniasync.change.FileTypeChange;
import com.siniatech.siniasync.change.IChange;
import com.siniatech.siniasync.change.IChangeContext;
import com.siniatech.siniautils.fn.IResponse1;

public class SynchReportPanel extends JComponent {

    private Long noOfFilesScanned = 0L;
    private Long noOfFilesMissing = 0L;
    private Long noOfFilesChanged = 0L;
    private Long noOfFileTypeChanges = 0L;

    private JLabel timeElapsedLabel;
    private JLabel noOfFilesScannedLabel;
    private JLabel noOfFilesMissingLabel;
    private JLabel noOfFilesChangedLabel;
    private JLabel noOfFileTypeChangesLabel;

    private long lastUpdate;
    private long synchStart;

    private IResponse1<IChange> synchMonitor;
    private final IChangeContext changeContext;

    public SynchReportPanel(IChangeContext changeContext) {
        this.changeContext = changeContext;
        setLayout( new BorderLayout() );

        synchMonitor = createSynchMonitor();

        lastUpdate = System.currentTimeMillis();

        timeElapsedLabel = new JLabel();
        noOfFilesScannedLabel = new JLabel();
        noOfFilesMissingLabel = new JLabel();
        noOfFilesChangedLabel = new JLabel();
        noOfFileTypeChangesLabel = new JLabel();

        JPanel numbersPanel = new JPanel( new GridLayout( 5, 2 ) );
        numbersPanel.add( new JLabel( "Time elapsed:" ) );
        numbersPanel.add( timeElapsedLabel );
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
                long elapsed = System.currentTimeMillis() - synchStart;
                long mins = TimeUnit.MILLISECONDS.toMinutes( elapsed );
                long secs = TimeUnit.MILLISECONDS.toSeconds( elapsed ) - TimeUnit.MINUTES.toSeconds( mins );
                timeElapsedLabel.setText( String.format( "%02d:%02d", mins, secs ) );
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
        synchStart = System.currentTimeMillis();
        updateView();
    }

    public IChangeContext getChangeContext() {
        return changeContext;
    }

}
