/*******************************************************************************
 * SiniaSync
 * Copyright (c) 2011-2 Siniatech Ltd  
 * http://www.siniatech.com/products/siniasync
 *
 * All rights reserved. This project and the accompanying materials are made 
 * available under the terms of the MIT License which can be found in the root  
 * of the project, and at http://www.opensource.org/licenses/mit-license.php
 *
 ******************************************************************************/
package com.siniatech.siniasync.gui.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.siniatech.dokz.DokzManager;
import com.siniatech.siniasync.change.ChangeOrchestrator;
import com.siniatech.siniasync.change.ChangeQueueingStrategy;
import com.siniatech.siniasync.change.IChange;
import com.siniatech.siniasync.change.IChangeContext;
import com.siniatech.siniasync.change.NoChange;
import com.siniatech.siniasync.manager.SyncManager;
import com.siniatech.siniautils.fn.IResponse0;

public class SiniaSyncFrame extends JFrame {

    private Path source;
    private Path target;
    private SyncManager syncManager;

    public SiniaSyncFrame() {
        final ExecutorService threadPool = Executors.newCachedThreadPool();

        getContentPane().setLayout( new BorderLayout() );

        DokzManager dokzContainer = new DokzManager();
        getContentPane().add( dokzContainer.asJComponent() );

        JPanel panel = new JPanel();
        panel.setLayout( new GridLayout( 2, 3 ) );
        dokzContainer.add( panel, "Synch" );

        final LoggingPanel loggingPanel = new LoggingPanel();
        dokzContainer.add( loggingPanel, "Log" );

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
                    loggingPanel.reportInfo( "Source path:" + source );
                }
            }
        } );
        panel.add( setSource );

        JButton setTarget = new JButton( "Set Target" );
        setTarget.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                JFileChooser targetChooser = new JFileChooser();
                targetChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
                int res = targetChooser.showOpenDialog( SiniaSyncFrame.this );
                if ( res == JFileChooser.APPROVE_OPTION ) {
                    target = targetChooser.getSelectedFile().toPath();
                    loggingPanel.reportInfo( "Target path:" + target );
                }
            }
        } );
        panel.add( setTarget );

        final SynchReportPanel synchReportPanel = new SynchReportPanel( new IChangeContext() {
            @Override
            public void reportSuccess( String string ) {
                loggingPanel.reportInfo( string );
            }

            @Override
            public void reportError( String string ) {
                loggingPanel.reportError( string );
            }

            @Override
            public ChangeQueueingStrategy getChangeQueueingStrategy( IChange change ) {
                return change instanceof NoChange ? ChangeQueueingStrategy.doNothing : ChangeQueueingStrategy.apply;
            }
        } );
        final JButton synchButton = new JButton( "Synch" );
        synchButton.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                threadPool.submit( new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SwingUtilities.invokeLater( new Runnable() {
                                @Override
                                public void run() {
                                    synchButton.setEnabled( false );
                                    synchReportPanel.startSynch();
                                }
                            } );
                            IResponse0 atEnd = new IResponse0() {
                                @Override
                                public void respond() {
                                    SwingUtilities.invokeLater( new Runnable() {
                                        @Override
                                        public void run() {
                                            synchButton.setEnabled( true );

                                        }
                                    } );
                                }
                            };
                            final ChangeOrchestrator changeOrchestrator = new ChangeOrchestrator( threadPool, synchReportPanel.getChangeContext() );
                            syncManager.determineChanges( source, target, changeOrchestrator, synchReportPanel.getSynchMonitor() );
                            changeOrchestrator.setSynchComplete( atEnd );

                        } catch ( Exception e1 ) {
                            e1.printStackTrace();
                        }
                    }
                } );
            }
        } );
        panel.add( synchButton );
        panel.add( synchReportPanel );
        setSize( 800, 600 );
        setVisible( true );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
