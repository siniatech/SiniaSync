package com.siniatech.siniasync;

import javax.swing.SwingUtilities;

import com.siniatech.siniasync.gui.SiniaSyncFrame;

public class Siniasync {
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new SiniaSyncFrame();
            }
        } );
    }
}
