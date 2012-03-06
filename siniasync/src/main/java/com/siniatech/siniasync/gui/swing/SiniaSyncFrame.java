package com.siniatech.siniasync.gui.swing;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.siniatech.siniasync.change.ChangeCollector;
import com.siniatech.siniasync.change.IChange;
import com.siniatech.siniasync.manager.SyncManager;
import com.siniatech.siniasync.monitor.SysoutProgressMonitor;

public class SiniaSyncFrame extends JFrame {

    private Path source;
    private Path target;
    private SyncManager syncManager;

    public SiniaSyncFrame() {
        final ExecutorService threadPool = Executors.newCachedThreadPool();

        getContentPane().setLayout( new GridLayout( 2, 3 ) );

        syncManager = new SyncManager();

        JButton setSource = new JButton( "Set Source" );
        setSource.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                JFileChooser sourceChooser = new JFileChooser();
                sourceChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
                int res = sourceChooser.showOpenDialog( SiniaSyncFrame.this );
                if ( res == JFileChooser.APPROVE_OPTION ) {
                    source = sourceChooser.getSelectedFile().toPath();
                    System.out.println( source );
                }
            }
        } );
        getContentPane().add( setSource );

        JButton setTarget = new JButton( "Set Target" );
        setTarget.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                JFileChooser targetChooser = new JFileChooser();
                targetChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
                int res = targetChooser.showOpenDialog( SiniaSyncFrame.this );
                if ( res == JFileChooser.APPROVE_OPTION ) {
                    target = targetChooser.getSelectedFile().toPath();
                    System.out.println( target );
                }
            }
        } );
        getContentPane().add( setTarget );

        final SynchReportPanel synchReportPanel = new SynchReportPanel();
        final JButton synchButton = new JButton( "Synch" );
        synchButton.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                try {
                    threadPool.submit( new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            SwingUtilities.invokeLater( new Runnable() {
                                @Override
                                public void run() {
                                    synchButton.setEnabled( false );
                                    synchReportPanel.startSynch();
                                }
                            } );
                            final ChangeCollector changeCollector = new ChangeCollector();
                            syncManager.determineChanges( source, target, changeCollector, synchReportPanel.getSynchMonitor() );
                            SwingUtilities.invokeLater( new Runnable() {
                                @Override
                                public void run() {
                                    int res = JOptionPane.showConfirmDialog( SiniaSyncFrame.this, "Proceed?" );
                                    if ( res == JOptionPane.YES_OPTION ) {
                                        for ( IChange change : changeCollector.getChanges() ) {
                                            change.apply( new SysoutProgressMonitor() );
                                        }
                                    }
                                    synchButton.setEnabled( true );
                                }
                            } );
                            return null;
                        }
                    } );
                } catch ( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        getContentPane().add( synchButton );
        getContentPane().add( synchReportPanel );
        setSize( 800, 600 );
        setVisible( true );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
