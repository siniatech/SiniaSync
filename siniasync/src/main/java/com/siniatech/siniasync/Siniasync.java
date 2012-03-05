package com.siniatech.siniasync;

import javax.swing.SwingUtilities;

import com.siniatech.siniasync.gui.swing.SiniaSyncFrame;

public class Siniasync {
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                new SiniaSyncFrame();
            }
        } );
    }
}
