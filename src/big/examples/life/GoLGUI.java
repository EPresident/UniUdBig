/*
 * Copyright (C) 2015 EPresident <prez_enquiry@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package big.examples.life;

import big.brs.BRS;
import big.brs.VerboseBFS;
import big.sim.TrueRandomSim;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Child;
import it.uniud.mads.jlibbig.core.std.Node;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class GoLGUI extends javax.swing.JFrame {

    private int rows, cols, liveCellsN, deadCellsN, cellsN, generation;
    private final LinkedList<Cell> cellsList;
    private TrueRandomSim sim;
    private Bigraph big;

    /**
     * Creates new form GoLGUI
     */
    public GoLGUI(int rows, int columns, Bigraph big) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GoLGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GoLGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GoLGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GoLGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        this.big = new BigraphBuilder(big).makeBigraph();
        this.rows = rows;
        cols = columns;
        cellsList = new LinkedList<>();
        liveCellsN = 0;
        deadCellsN = rows * cols;
        cellsN = rows * cols;
        generation = 0;
        sim = new TrueRandomSim(big, new BRS(new VerboseBFS(), GameOfLife.RULES));

        initComponents();
        setVisible(true);
        parseBigraph();
        System.out.println("init: "+cellsN+" cells, "+liveCellsN+" live and "+deadCellsN+" dead.");
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        //super.paint(g);
        paintComponents(g);
        draw();
    }

    public void draw() {
        Graphics2D g = (Graphics2D) drawPanel.getGraphics();

        drawCells(g);
        drawGrid(g);
    }

    private void drawGrid(Graphics2D g) {
        int w = drawPanel.getWidth(),
                h = drawPanel.getHeight();
        int cs = Math.min(w, h) / Math.min(rows, cols); // Cell (edge) Size 
        for (int i = 0; i <= cols; i++) {
            g.drawLine(0, i * cs, cs * cols, i * cs);
        }
        for (int i = 0; i <= rows; i++) {
            g.drawLine(i * cs, 0, i * cs, cs * rows);
        }
    }

    private void drawCells(Graphics2D g) {
        int w = drawPanel.getWidth(),
                h = drawPanel.getHeight();
        int cs = Math.min(w, h) / Math.min(rows, cols); // Cell (edge) Size 
        Color pc = g.getColor();
        // Draw Dead Cells
        g.setColor(new Color(0, 54, 6));
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                g.fillRect(i * cs, j * cs, cs, cs);
            }
        }
        // Draw Live Cells
        g.setColor(new Color(49, 214, 69));
        for (Cell c : cellsList) {
            g.fillRect(c.x * cs, c.y * cs, cs, cs);
        }
        g.setColor(pc);
    }

    private void update() {
        System.out.print("Stepping in Sim...");
        sim.step();
        System.out.println(" done.");
        Bigraph step = sim.getCurrentState();
        if (step != null) {
            deadCellsN = cellsN;
            liveCellsN=0;
            cellsList.clear();
            generation++;

            big = step;
            parseBigraph();
            repaint();
            System.out.println("updated: "+cellsN+" cells, "+liveCellsN+" live and "+deadCellsN+" dead.");
        } else {
            System.out.println("Done!");
            stepBtn.setEnabled(false);
        }
    }

    private void parseBigraph() {
        for (Node n : big.getNodes()) {
            if (n.isNode() && n.getControl().equals(GameOfLife.SIGNATURE.getByName("cell"))) {
                // Got a cell
                for (Child c : n.getChildren()) {
                    if (c.isNode()) {
                        Node cn = (Node) c;
                        if (cn.getControl().equals(GameOfLife.SIGNATURE.getByName("life"))) {
                            // Got a live cell
                            String[] s = n.getProperty("Position").get().toString().split(",");
                            int x = Integer.parseInt(s[0]),
                                    y = Integer.parseInt(s[1]);
                            cellsList.add(new Cell(x, y, true));
                            liveCellsN++;
                            deadCellsN--;
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        drawPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        stepBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Game of Life GUI");

        drawPanel.setBackground(new java.awt.Color(153, 153, 153));
        drawPanel.setFocusable(false);
        drawPanel.setPreferredSize(new java.awt.Dimension(800, 500));

        javax.swing.GroupLayout drawPanelLayout = new javax.swing.GroupLayout(drawPanel);
        drawPanel.setLayout(drawPanelLayout);
        drawPanelLayout.setHorizontalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        drawPanelLayout.setVerticalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );

        getContentPane().add(drawPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setBackground(new java.awt.Color(102, 102, 102));
        controlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controls", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), new java.awt.Color(255, 255, 255))); // NOI18N
        controlPanel.setPreferredSize(new java.awt.Dimension(800, 60));

        stepBtn.setText("Step");
        stepBtn.setAlignmentY(0.0F);
        stepBtn.setMargin(new java.awt.Insets(1, 1, 1, 1));
        stepBtn.setMaximumSize(new java.awt.Dimension(112, 50));
        stepBtn.setMinimumSize(new java.awt.Dimension(30, 20));
        stepBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addComponent(stepBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 323, Short.MAX_VALUE))
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addComponent(stepBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(controlPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stepBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepBtnActionPerformed
        update();
    }//GEN-LAST:event_stepBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel drawPanel;
    private javax.swing.JButton stepBtn;
    // End of variables declaration//GEN-END:variables

    private class Cell {

        private final int x, y;
        private final boolean alive;

        Cell(int x, int y, boolean a) {
            this.x = x;
            this.y = y;
            alive = a;
        }
    }

}
