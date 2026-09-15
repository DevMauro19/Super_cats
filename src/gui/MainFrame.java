package gui;

import Exceptions.EEntradaInvalida;
import Exceptions.ENumeroNegativo;
import algorithms.mission1.BFSDFSSolver;
import algorithms.mission1.PathResult;
import algorithms.mission2.Dijkstra;
import algorithms.mission2.DijkstraResult;
import algorithms.mission3.BellmanFord;
import algorithms.mission3.BellmanFordResult;
import algorithms.mission3.FloydWarshall;
import algorithms.mission3.Mission3Result;
import algorithms.mission3.Mission3Solver;
import algorithms.mission4.Kruskal;
import algorithms.mission4.Union;
import io.Input;
import io.MisionDosCaso;
import io.MisionTresCaso;
import io.MisionUnoCaso;
import io.Output;
import model.Edge;
import model.Graph;
import model.Grid;
import model.Punto;
import model.WeightedEdge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class MainFrame extends JFrame {

    // ---- Theme palette -------------------------------------------------
    private static final Color BG_DARK = new Color(18, 18, 18);
    private static final Color PANEL_DARK = new Color(30, 30, 30);
    private static final Color PANEL_LIGHT = new Color(45, 45, 45);
    private static final Color CARD_BORDER = new Color(70, 70, 70);
    private static final Color ORANGE = new Color(255, 140, 32);
    private static final Color ORANGE_SOFT = new Color(245, 176, 76);
    private static final Color WHITE = new Color(245, 245, 245);
    private static final Color MUTED = new Color(180, 180, 180);
    private static final Color CAT_BLACK = new Color(20, 20, 20);
    private static final Color CAT_RED = new Color(145, 36, 18);
    private static final Color CAT_GREEN = new Color(96, 167, 82);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_TAB = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_MONO_IN = new Font("Consolas", Font.PLAIN, 13);
    private static final Font FONT_MONO_OUT = new Font("Consolas", Font.PLAIN, 12);
    private static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 12);

    private static final int[] MISSION_ICON_TYPES = {0, 1, 2, 3};
    private static final String[] MISSION_NAMES = {"Campo minado", "Cuentas Claude", "Reserva de churun", "Reconexión"};

    private final CardLayout missionCardLayout = new CardLayout();
    private final JPanel missionCards = new JPanel(missionCardLayout);
    private final Map<Integer, TabButton> tabButtons = new LinkedHashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    public MainFrame() {
        super("Super Cats - The Feline Graph Chronicles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configureUiManagerDefaults();
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(0, 16, 16, 16));

        body.add(buildTabBar(), BorderLayout.NORTH);

        missionCards.setOpaque(false);
        for (int i = 1; i <= 4; i++) {
            missionCards.add(buildMissionPanel(i), "mission" + i);
        }
        body.add(missionCards, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        selectMission(1);

        setSize(1280, 900);
        setLocationRelativeTo(null);
    }

    private void configureUiManagerDefaults() {
        UIManager.put("TitledBorder.titleColor", ORANGE_SOFT);
        UIManager.put("ToolTip.background", PANEL_LIGHT);
        UIManager.put("ToolTip.foreground", WHITE);
        UIManager.put("SplitPane.background", BG_DARK);
        UIManager.put("SplitPaneDivider.draggingColor", ORANGE);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(18, 16, 12, 16));

        JLabel title = new JLabel("Super Cats — The Feline Graph Chronicles", new CatIcon(28), SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(WHITE);
        header.add(title, BorderLayout.CENTER);

        JLabel subtitle = new JLabel("BFS / DFS  •  Dijkstra  •  Floyd-Warshall  •  Bellman-Ford  •  Kruskal", SwingConstants.CENTER);
        subtitle.setForeground(ORANGE_SOFT);
        subtitle.setFont(FONT_SUBTITLE);
        header.add(subtitle, BorderLayout.SOUTH);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_DARK);
        wrap.add(header, BorderLayout.CENTER);

        JPanel accentLine = new JPanel();
        accentLine.setPreferredSize(new Dimension(10, 3));
        accentLine.setBackground(ORANGE);
        wrap.add(accentLine, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildTabBar() {
        JPanel bar = new JPanel(new GridLayout(1, 4, 10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(4, 0, 14, 0));

        for (int i = 1; i <= 4; i++) {
            String label = "Misión " + i;
            TabButton tab = new TabButton(label, MISSION_NAMES[i - 1], MISSION_ICON_TYPES[i - 1]);
            int missionId = i;
            tab.addActionListener(e -> selectMission(missionId));
            tabButtons.put(i, tab);
            bar.add(tab);
        }
        return bar;
    }


    private void selectMission(int missionId) {
        missionCardLayout.show(missionCards, "mission" + missionId);
        for (Map.Entry<Integer, TabButton> entry : tabButtons.entrySet()) {
            entry.getValue().setSelected(entry.getKey() == missionId);
        }
    }

    private JPanel buildMissionPanel(int missionId) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JTextArea inputArea = new JTextArea();
        styleTextArea(inputArea, PANEL_LIGHT, true);
        inputArea.setFont(FONT_MONO_IN);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        styleTextArea(outputArea, new Color(18, 18, 18), false);
        outputArea.setFont(FONT_MONO_OUT);

        GraphPreviewPanel preview = new GraphPreviewPanel();

        JButton sampleBtn = createButton("Cargar ejemplo", false);
        JButton runBtn = createButton("Ejecutar", true);
        JButton clearBtn = createButton("Limpiar", false);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        controls.setOpaque(false);
        controls.add(sampleBtn);
        controls.add(runBtn);
        controls.add(clearBtn);

        JPanel left = new JPanel(new BorderLayout(10, 10));
        left.setOpaque(false);
        left.add(sectionLabel("Entrada del caso de prueba"), BorderLayout.NORTH);
        left.add(cardWrap(new JScrollPane(inputArea)), BorderLayout.CENTER);

        JPanel outputPanel = new JPanel(new BorderLayout(8, 8));
        outputPanel.setOpaque(false);
        outputPanel.add(sectionLabel("Resultado"), BorderLayout.NORTH);
        outputPanel.add(cardWrap(new JScrollPane(outputArea)), BorderLayout.CENTER);
        left.add(outputPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, preview);
        split.setResizeWeight(0.45);
        split.setDividerSize(10);
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(BG_DARK);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);

        sampleBtn.addActionListener(e -> {
            inputArea.setText(sampleForMission(missionId));
            outputArea.setText("");
            preview.clear();
        });

        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
            preview.clear();
        });

        runBtn.addActionListener(e -> runMission(missionId, inputArea, outputArea, preview));

        inputArea.setText(sampleForMission(missionId));
        return panel;
    }

    private JPanel cardWrap(JComponent content) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(PANEL_LIGHT);
        wrap.setBorder(new LineBorder(CARD_BORDER, 1, true));
        content.setBorder(new EmptyBorder(4, 4, 4, 4));
        wrap.add(content, BorderLayout.CENTER);
        return wrap;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ORANGE_SOFT);
        label.setFont(FONT_LABEL);
        return label;
    }

    private void styleTextArea(JTextArea area, Color bg, boolean editable) {
        area.setBackground(bg);
        area.setForeground(WHITE);
        area.setCaretColor(ORANGE);
        area.setLineWrap(false);
        area.setBorder(new EmptyBorder(6, 6, 6, 6));
    }

    private void runMission(int missionId, JTextArea inputArea, JTextArea outputArea, GraphPreviewPanel preview) {
        try {
            switch (missionId) {
                case 1:
                    List<MisionUnoCaso> casos1 = Input.leerMision1(inputArea.getText());
                    StringBuilder sb1 = new StringBuilder();
                    if (casos1.isEmpty()) {
                        sb1.append("No se encontraron casos de prueba.");
                    } else {
                        for (int i = 0; i < casos1.size(); i++) {
                            MisionUnoCaso caso = casos1.get(i);
                            PathResult bfs = BFSDFSSolver.bfs(caso.getGrid(), caso.getInicio(), caso.getDestino());
                            PathResult dfs = BFSDFSSolver.dfs(caso.getGrid(), caso.getInicio(), caso.getDestino());
                            sb1.append(Output.formatearMision1(i + 1, bfs, dfs)).append(System.lineSeparator());
                            if (i == 0) {
                                preview.showMission1(caso.getGrid(), caso.getInicio(), caso.getDestino(), bfs.getPath(), dfs.getPath());
                            }
                        }
                    }
                    outputArea.setText(sb1.toString());
                    break;

                case 2:
                    List<MisionDosCaso> casos2 = Input.leerMision2(inputArea.getText());
                    StringBuilder sb2 = new StringBuilder();
                    if (casos2.isEmpty()) {
                        sb2.append("No se encontraron casos de prueba.");
                    } else {
                        List<DijkstraResult> resultados2 = new ArrayList<>();
                        for (int i = 0; i < casos2.size(); i++) {
                            MisionDosCaso caso = casos2.get(i);
                            DijkstraResult result = Dijkstra.resolver(caso.getGrafo(), caso.getOrigen(), caso.getDestino());
                            resultados2.add(result);
                            sb2.append(Output.formatearMision2(i + 1, result)).append(System.lineSeparator());
                        }
                        // Enviar todos los casos a la vista previa
                        preview.showMission2Casos(casos2, resultados2);
                    }
                    outputArea.setText(sb2.toString());
                    break;

                case 3:
                    List<MisionTresCaso> casos3 = Input.leerMision3(inputArea.getText());
                    StringBuilder sb3 = new StringBuilder();
                    if (casos3.isEmpty()) {
                        sb3.append("No se encontraron casos de prueba.");
                    } else {
                        List<Mission3Result> resultados3 = new ArrayList<>();
                        for (int i = 0; i < casos3.size(); i++) {
                            MisionTresCaso caso = casos3.get(i);
                            Mission3Result result = Mission3Solver.resolver(caso.getGrafo(), caso.getOrigen(), caso.getDestino());
                            resultados3.add(result);
                            sb3.append(Output.formatearMision3(i + 1, result)).append(System.lineSeparator());
                        }
                        // Enviar la lista de casos completa
                        preview.showMission3Casos(casos3, resultados3);
                    }
                    outputArea.setText(sb3.toString());
                    break;

                case 4:
                    List<Graph> grafos4 = Input.leerMission4(inputArea.getText());
                    StringBuilder sb4 = new StringBuilder();
                    if (grafos4.isEmpty()) {
                        sb4.append("No se encontraron casos de prueba.");
                    } else {
                        for (int i = 0; i < grafos4.size(); i++) {
                            Graph grafo = grafos4.get(i);
                            Kruskal.Resultado resultado = Kruskal.ejecutar(grafo);
                            sb4.append(Output.formatearMision4(i + 1, resultado)).append(System.lineSeparator());
                            if (i == 0) {
                                preview.showMission4(grafo, resultado);
                            }
                        }
                    }
                    outputArea.setText(sb4.toString());
                    break;

                default:
                    outputArea.setText("Misión no soportada.");
            }
        } catch (EEntradaInvalida | ENumeroNegativo e) {
            outputArea.setText("Entrada inválida: " + e.getMessage());
            preview.clear();
        }
    }

    private String sampleForMission(int missionId) {
        switch (missionId) {
            case 1:
                return Input.EJEMPLO_MISSION1;
            case 2:
                return Input.EJEMPLO_MISSION2;
            case 3:
                return Input.EJEMPLO_MISSION3;
            case 4:
                return Input.EJEMPLO_MISSION4;
            default:
                return "";
        }
    }

    private JButton createButton(String text, boolean primary) {
        ThemedButton button = new ThemedButton(text, primary);
        button.setFont(FONT_BTN);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static final class ThemedButton extends JButton {
        private final boolean primary;
        private boolean hover = false;

        ThemedButton(String text, boolean primary) {
            super(text);
            this.primary = primary;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(primary ? CAT_BLACK : ORANGE_SOFT);
            setBorder(new EmptyBorder(8, 16, 8, 16));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            if (primary) {
                g2.setColor(hover ? ORANGE_SOFT : ORANGE);
                g2.fill(shape);
            } else {
                g2.setColor(hover ? PANEL_LIGHT : PANEL_DARK);
                g2.fill(shape);
                g2.setColor(ORANGE);
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(shape);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class MissionIcon implements Icon {
        private final int type;
        private Color color;

        MissionIcon(int type, Color color) {
            this.type = type;
            this.color = color;
        }

        void setColor(Color color) {
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return 22;
        }

        @Override
        public int getIconHeight() {
            return 22;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = x + 11;
            int cy = y + 11;

            switch (type) {
                case 0:
                    g2.fill(new Ellipse2D.Double(x + 4, y + 7, 13, 13));
                    g2.draw(new Line2D.Double(x + 14, y + 7, x + 18, y + 3));
                    g2.draw(new Line2D.Double(x + 18, y + 3, x + 20, y + 4));
                    break;

                case 1:
                    g2.drawRoundRect(x + 2, y + 4, 18, 15, 2, 2);
                    g2.drawLine(x + 8, y + 4, x + 8, y + 19);
                    g2.drawLine(x + 14, y + 4, x + 14, y + 19);
                    g2.fill(new Ellipse2D.Double(cx - 2.5, cy - 3, 5, 5));
                    g2.draw(new Line2D.Double(cx, cy + 2, cx, cy + 6));
                    break;

                case 2:
                    g2.drawRoundRect(x + 4, y + 5, 12, 13, 2, 2);
                    g2.drawArc(x + 14, y + 7, 7, 8, -90, 180);
                    g2.drawLine(x + 3, y + 19, x + 18, y + 19);
                    g2.drawLine(x + 8, y + 3, x + 8, y + 6);
                    g2.drawLine(x + 12, y + 3, x + 12, y + 6);
                    break;

                default:
                    g2.drawLine(x + 8, y + 2, x + 8, y + 7);
                    g2.drawLine(x + 14, y + 2, x + 14, y + 7);
                    g2.drawRoundRect(x + 5, y + 6, 12, 8, 3, 3);
                    g2.drawLine(x + 11, y + 14, x + 11, y + 20);
                    break;
            }

            g2.dispose();
        }
    }

    private static final class CatIcon implements Icon {
        private final int size;

        CatIcon(int size) {
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = ORANGE;
            Color line = c != null && c.getForeground() != null ? c.getForeground() : WHITE;

            int s = size;
            Polygon ears = new Polygon(
                    new int[]{x + s / 5, x + s / 3, x + s / 2, x + 2 * s / 3, x + 4 * s / 5},
                    new int[]{y + 2 * s / 5, y + s / 5, y + 2 * s / 5, y + s / 5, y + 2 * s / 5},
                    5
            );
            g2.setColor(fill);
            g2.fill(ears);
            g2.fill(new Ellipse2D.Double(x + s / 5, y + s / 3, 3 * s / 5, 3 * s / 5));

            g2.setColor(line);
            g2.setStroke(new BasicStroke(Math.max(1.2f, s / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            double eye = Math.max(2, s / 10.0);
            g2.fill(new Ellipse2D.Double(x + s * 0.35, y + s * 0.52, eye, eye));
            g2.fill(new Ellipse2D.Double(x + s * 0.58, y + s * 0.52, eye, eye));
            g2.draw(new Arc2D.Double(x + s * 0.40, y + s * 0.58, s * 0.20, s * 0.16, 200, 140, Arc2D.OPEN));

            g2.drawLine((int) (x + s * 0.28), (int) (y + s * 0.62), (int) (x + s * 0.05), (int) (y + s * 0.56));
            g2.drawLine((int) (x + s * 0.28), (int) (y + s * 0.68), (int) (x + s * 0.05), (int) (y + s * 0.70));
            g2.drawLine((int) (x + s * 0.72), (int) (y + s * 0.62), (int) (x + s * 0.95), (int) (y + s * 0.56));
            g2.drawLine((int) (x + s * 0.72), (int) (y + s * 0.68), (int) (x + s * 0.95), (int) (y + s * 0.70));

            g2.dispose();
        }
    }

    private static final class TabButton extends JButton {
        private boolean selected = false;

        TabButton(String label, String tooltip, int iconType) {
            super(label);
            setToolTipText(tooltip);
            setIcon(new MissionIcon(iconType, ORANGE_SOFT));
            setIconTextGap(8);
            setFont(FONT_TAB);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 8, 10, 8));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            setForeground(selected ? CAT_BLACK : WHITE);
            if (getIcon() instanceof MissionIcon) {
                ((MissionIcon) getIcon()).setColor(selected ? CAT_BLACK : ORANGE_SOFT);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            if (selected) {
                g2.setColor(ORANGE);
            } else if (getModel().isRollover()) {
                g2.setColor(PANEL_LIGHT);
            } else {
                g2.setColor(PANEL_DARK);
            }
            g2.fill(shape);
            if (!selected) {
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(shape);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class GraphPreviewPanel extends JPanel {
        private final CardLayout previewCardLayout = new CardLayout();
        private final JPanel previewCards = new JPanel(previewCardLayout);
        private final JPanel contentGrid = new JPanel(new GridLayout(1, 3, 10, 10));
        private final JPanel controlsBar;
        private final JPanel topBar = new JPanel(new BorderLayout(0, 6));
        private final JPanel selectorBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        private final JButton playPauseBtn;
        private final JSlider speedSlider;
        private final JLabel stepLabel;

        private Timer animTimer;
        private Runnable animTick;
        private Runnable animReset;
        private BooleanSupplier animFinished;

        private List<int[]> bfsOrder = Collections.emptyList();
        private List<int[]> dfsOrder = Collections.emptyList();
        private final int[] bfsReveal = {0};
        private final int[] dfsReveal = {0};
        private JComponent bfsCanvas;
        private JComponent dfsCanvas;

        GraphPreviewPanel() {
            setLayout(new BorderLayout(8, 8));
            setBackground(PANEL_DARK);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(CARD_BORDER, 1, true),
                    new EmptyBorder(10, 12, 12, 12)
            ));

            JLabel header = new JLabel("Mini pantalla de evolución", new CatIcon(18), SwingConstants.LEFT);
            header.setIconTextGap(6);
            header.setForeground(ORANGE_SOFT);
            header.setFont(FONT_LABEL);
            header.setBorder(new EmptyBorder(0, 0, 0, 0));
            selectorBar.setOpaque(false);
            selectorBar.setVisible(false);
            topBar.setOpaque(false);
            topBar.add(header, BorderLayout.NORTH);
            topBar.add(selectorBar, BorderLayout.SOUTH);
            add(topBar, BorderLayout.NORTH);

            contentGrid.setOpaque(false);

            previewCards.setOpaque(false);
            previewCards.add(buildPlaceholder(), "empty");
            previewCards.add(contentGrid, "content");
            add(previewCards, BorderLayout.CENTER);

            previewCardLayout.show(previewCards, "empty");

            playPauseBtn = miniButton("Play");
            JButton resetBtn = miniButton("Reset");
            speedSlider = new JSlider(1, 5, 3);
            speedSlider.setOpaque(false);
            speedSlider.setPreferredSize(new Dimension(90, 20));

            JLabel velLabel = new JLabel("Velocidad");
            velLabel.setForeground(WHITE);
            velLabel.setFont(FONT_SUBTITLE);

            stepLabel = new JLabel("");
            stepLabel.setForeground(WHITE);
            stepLabel.setFont(FONT_SUBTITLE);

            controlsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            controlsBar.setBackground(PANEL_LIGHT);
            controlsBar.setOpaque(true);
            controlsBar.setBorder(new EmptyBorder(6, 8, 6, 8));
            controlsBar.add(playPauseBtn);
            controlsBar.add(resetBtn);
            controlsBar.add(velLabel);
            controlsBar.add(speedSlider);
            controlsBar.add(stepLabel);
            controlsBar.setVisible(false);
            add(controlsBar, BorderLayout.SOUTH);

            playPauseBtn.addActionListener(e -> toggleAnimation());
            resetBtn.addActionListener(e -> resetAnimation());
            speedSlider.addChangeListener(e -> {
                if (animTimer != null) {
                    animTimer.setDelay(delayForSpeed(speedSlider.getValue()));
                }
            });
        }

        private JButton miniButton(String text) {
            JButton button = new JButton(text);
            button.setFont(FONT_BTN);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false); // Quita la caja blanca por defecto de Swing
            button.setBorderPainted(false);
            button.setOpaque(false);
            button.setForeground(ORANGE_SOFT);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setBorder(new EmptyBorder(6, 14, 6, 14));

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.repaint();
                }
            });

            // Dibujado personalizado con bordes redondeados y contraste oscuro
            button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    boolean hover = button.getModel().isRollover();
                    RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, button.getWidth() - 1, button.getHeight() - 1, 8, 8);

                    g2.setColor(hover ? PANEL_LIGHT : PANEL_DARK);
                    g2.fill(shape);

                    g2.setColor(ORANGE);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.draw(shape);

                    g2.dispose();
                    super.paint(g, c);
                }
            });

            return button;
        }

        // Retardos en milisegundos incrementados para hacer las animaciones más lentas
        // Modifica este método dentro de GraphPreviewPanel en MainFrame.java
        private int delayForSpeed(int speed) {
            // Escala de retardos en milisegundos:
            // Nivel 1: Muy pausado (3.0 segundos por paso)
            // Nivel 3: Velocidad media (1.5 segundos por paso)
            // Nivel 5: Moderado (0.6 segundos por paso)
            int[] delays = {3000, 2200, 1500, 1000, 600};
            return delays[Math.max(1, Math.min(5, speed)) - 1];
        }

        private JPanel buildPlaceholder() {
            JPanel placeholder = new JPanel(new GridBagLayout());
            placeholder.setOpaque(false);
            JLabel label = new JLabel("<html><div style='text-align:center;'>"
                    + "Pulsa <b>Ejecutar</b> para ver el mapa<br>o el grafo de la primera prueba"
                    + "</div></html>", new CatIcon(30), SwingConstants.CENTER);
            label.setIconTextGap(10);
            label.setForeground(MUTED);
            label.setFont(FONT_SUBTITLE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            placeholder.add(label);
            return placeholder;
        }

        void clear() {
            stopAnimation();
            animTick = null;
            animReset = null;
            animFinished = null;
            selectorBar.removeAll();
            selectorBar.setVisible(false);
            controlsBar.setVisible(false);
            contentGrid.removeAll();
            previewCardLayout.show(previewCards, "empty");
            previewCards.revalidate();
            previewCards.repaint();
        }

        void showMission1(Grid grid, Punto inicio, Punto destino, List<Punto> bfsPath, List<Punto> dfsPath) {
            stopAnimation();
            contentGrid.removeAll();

            bfsOrder = computeBfsVisitOrder(grid, inicio, destino);
            dfsOrder = computeDfsVisitOrder(grid, inicio, destino);
            bfsReveal[0] = 0;
            dfsReveal[0] = 0;

            Set<String> bfsPathCells = cellSet(bfsPath);
            Set<String> dfsPathCells = cellSet(dfsPath);

            contentGrid.add(createGridCard("Mapa", grid, inicio, destino, null));
            bfsCanvas = animatedGridCanvas(grid, inicio, destino, bfsOrder, bfsReveal, bfsPathCells);
            dfsCanvas = animatedGridCanvas(grid, inicio, destino, dfsOrder, dfsReveal, dfsPathCells);
            contentGrid.add(wrapCard("BFS (explorando)", bfsCanvas));
            contentGrid.add(wrapCard("DFS (explorando)", dfsCanvas));

            controlsBar.setVisible(true);
            showContent();

            Runnable tick = () -> {
                if (bfsReveal[0] < bfsOrder.size()) bfsReveal[0]++;
                if (dfsReveal[0] < dfsOrder.size()) dfsReveal[0]++;
                bfsCanvas.repaint();
                dfsCanvas.repaint();
                stepLabel.setText("BFS " + bfsReveal[0] + "/" + bfsOrder.size()
                        + "   DFS " + dfsReveal[0] + "/" + dfsOrder.size());
            };
            Runnable reset = () -> {
                bfsReveal[0] = 0;
                dfsReveal[0] = 0;
                bfsCanvas.repaint();
                dfsCanvas.repaint();
            };
            BooleanSupplier finished = () -> bfsReveal[0] >= bfsOrder.size() && dfsReveal[0] >= dfsOrder.size();
            startAnimationEngine(tick, reset, finished);
        }

        private void startAnimationEngine(Runnable tick, Runnable reset, BooleanSupplier finished) {
            stopAnimation();
            this.animTick = tick;
            this.animReset = reset;
            this.animFinished = finished;
            animTimer = new Timer(delayForSpeed(speedSlider.getValue()), e -> {
                animTick.run();
                if (animFinished.getAsBoolean()) {
                    stopAnimation();
                    playPauseBtn.setText("Play");
                }
            });
            animTimer.start();
            playPauseBtn.setText("Pause");
        }

        private void toggleAnimation() {
            if (animTick == null) return;
            if (animTimer != null && animTimer.isRunning()) {
                animTimer.stop();
                playPauseBtn.setText("Play");
                return;
            }
            if (animFinished != null && animFinished.getAsBoolean()) {
                animReset.run();
            }
            startAnimationEngine(animTick, animReset, animFinished);
        }

        private void resetAnimation() {
            stopAnimation();
            if (animReset != null) animReset.run();
            stepLabel.setText("");
            playPauseBtn.setText("Play");
        }

        private void stopAnimation() {
            if (animTimer != null) {
                animTimer.stop();
                animTimer = null;
            }
        }

        private List<int[]> computeBfsVisitOrder(Grid grid, Punto inicio, Punto destino) {
            List<int[]> order = new ArrayList<>();
            if (grid.hayBomba(inicio.getFila(), inicio.getColumna())
                    || grid.hayBomba(destino.getFila(), destino.getColumna())) {
                return order;
            }
            int rows = grid.getFilas();
            int cols = grid.getColumnas();
            boolean[][] visited = new boolean[rows][cols];
            Deque<int[]> queue = new ArrayDeque<>();
            queue.add(new int[]{inicio.getFila(), inicio.getColumna()});
            visited[inicio.getFila()][inicio.getColumna()] = true;
            int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            while (!queue.isEmpty()) {
                int[] cur = queue.poll();
                order.add(cur);
                if (cur[0] == destino.getFila() && cur[1] == destino.getColumna()) {
                    break;
                }
                for (int[] d : deltas) {
                    int nr = cur[0] + d[0];
                    int nc = cur[1] + d[1];
                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                    if (visited[nr][nc] || grid.hayBomba(nr, nc)) continue;
                    visited[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
            return order;
        }

        private List<int[]> computeDfsVisitOrder(Grid grid, Punto inicio, Punto destino) {
            List<int[]> order = new ArrayList<>();
            if (grid.hayBomba(inicio.getFila(), inicio.getColumna())
                    || grid.hayBomba(destino.getFila(), destino.getColumna())) {
                return order;
            }
            int rows = grid.getFilas();
            int cols = grid.getColumnas();
            boolean[][] visited = new boolean[rows][cols];
            Deque<int[]> stack = new ArrayDeque<>();
            stack.push(new int[]{inicio.getFila(), inicio.getColumna()});
            int[][] deltasReversed = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

            while (!stack.isEmpty()) {
                int[] cur = stack.pop();
                if (visited[cur[0]][cur[1]]) continue;
                visited[cur[0]][cur[1]] = true;
                order.add(cur);
                if (cur[0] == destino.getFila() && cur[1] == destino.getColumna()) {
                    break;
                }
                for (int[] d : deltasReversed) {
                    int nr = cur[0] + d[0];
                    int nc = cur[1] + d[1];
                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                    if (visited[nr][nc] || grid.hayBomba(nr, nc)) continue;
                    stack.push(new int[]{nr, nc});
                }
            }
            return order;
        }

        private Set<String> cellSet(List<Punto> path) {
            Set<String> set = new HashSet<>();
            if (path != null) {
                for (Punto p : path) {
                    set.add(p.getFila() + "," + p.getColumna());
                }
            }
            return set;
        }

        private JComponent animatedGridCanvas(Grid grid, Punto inicio, Punto destino,
                                              List<int[]> order, int[] reveal, Set<String> pathCells) {
            JPanel canvas = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int rows = grid.getFilas();
                    int cols = grid.getColumnas();
                    int cell = Math.min((getWidth() - 20) / Math.max(cols, 1), (getHeight() - 20) / Math.max(rows, 1));
                    int offsetX = (getWidth() - cols * cell) / 2;
                    int offsetY = (getHeight() - rows * cell) / 2;

                    boolean finished = reveal[0] >= order.size();
                    int[][] visitIndex = new int[rows][cols];
                    for (int[] row : visitIndex) java.util.Arrays.fill(row, -1);
                    for (int i = 0; i < reveal[0] && i < order.size(); i++) {
                        int[] c = order.get(i);
                        visitIndex[c[0]][c[1]] = i;
                    }

                    for (int r = 0; r < rows; r++) {
                        for (int c = 0; c < cols; c++) {
                            int x = offsetX + c * cell;
                            int y = offsetY + r * cell;
                            Color color = new Color(50, 50, 50);
                            if (visitIndex[r][c] >= 0) {
                                if (finished && pathCells.contains(r + "," + c)) {
                                    color = ORANGE;
                                } else if (finished) {
                                    color = new Color(80, 80, 120);
                                } else {
                                    color = new Color(130, 95, 210);
                                }
                            }
                            if (grid.hayBomba(r, c)) {
                                color = CAT_RED;
                            }
                            if (r == inicio.getFila() && c == inicio.getColumna()) {
                                color = CAT_GREEN;
                            }
                            if (r == destino.getFila() && c == destino.getColumna() && visitIndex[r][c] >= 0) {
                                color = finished ? ORANGE_SOFT : color;
                            } else if (r == destino.getFila() && c == destino.getColumna()) {
                                color = ORANGE_SOFT;
                            }
                            g2.setColor(color);
                            g2.fillRect(x, y, cell, cell);
                            g2.setColor(new Color(28, 28, 28));
                            g2.drawRect(x, y, cell, cell);
                        }
                    }
                    g2.dispose();
                }
            };
            canvas.setOpaque(false);
            return canvas;
        }

        void showMission2(Graph graph, int origen, int destino, DijkstraResult result) {
            stopAnimation();
            contentGrid.removeAll();

            List<Integer> settleOrder = computeDijkstraSettleOrder(graph, origen, destino);
            int[] reveal = {0};
            Set<Integer> pathNodes = pathNodes(result.getCamino());
            Set<String> pathEdges = consecutiveEdgeKeys(result.getCamino(), false);

            contentGrid.add(createGraphCard("Original", graph, Collections.emptySet(), Collections.emptySet(), origen, destino));
            JComponent canvas = animatedDijkstraCanvas(graph, origen, destino, settleOrder, reveal, pathNodes, pathEdges);
            contentGrid.add(wrapCard("Dijkstra (procesando)", canvas));
            contentGrid.add(createGraphCard("Resultado", graph, pathNodes, pathEdges, origen, destino));

            controlsBar.setVisible(true);
            showContent();

            Runnable tick = () -> {
                if (reveal[0] < settleOrder.size()) reveal[0]++;
                canvas.repaint();
                stepLabel.setText("Nodo asentado " + reveal[0] + "/" + settleOrder.size());
            };
            Runnable reset = () -> {
                reveal[0] = 0;
                canvas.repaint();
            };
            BooleanSupplier finished = () -> reveal[0] >= settleOrder.size();
            startAnimationEngine(tick, reset, finished);
        }
        public void showMission2Casos(List<MisionDosCaso> casos, List<DijkstraResult> resultados) {
            if (casos == null || casos.isEmpty()) return;
            if (resultados == null || resultados.size() != casos.size()) return;

            selectorBar.removeAll();
            selectorBar.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
            selectorBar.setOpaque(false);

            for (int i = 0; i < casos.size(); i++) {
                int index = i;
                JButton btnCaso = miniButton("Caso #" + (i + 1));
                btnCaso.addActionListener(e -> {
                    MisionDosCaso casoSel = casos.get(index);
                    DijkstraResult resSel = resultados.get(index);
                    showMission2(casoSel.getGrafo(), casoSel.getOrigen(), casoSel.getDestino(), resSel);
                });
                selectorBar.add(btnCaso);
            }

            selectorBar.setVisible(true);
            topBar.revalidate();
            topBar.repaint();

            MisionDosCaso primerCaso = casos.get(0);
            DijkstraResult primerRes = resultados.get(0);
            showMission2(primerCaso.getGrafo(), primerCaso.getOrigen(), primerCaso.getDestino(), primerRes);
        }

        public void showMission3Casos(List<MisionTresCaso> casos, List<Mission3Result> resultados) {
            if (casos == null || casos.isEmpty()) return;
            if (resultados == null || resultados.size() != casos.size()) return;

            selectorBar.removeAll();
            selectorBar.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
            selectorBar.setOpaque(false);

            for (int i = 0; i < casos.size(); i++) {
                int index = i;
                JButton btnCaso = miniButton("Caso #" + (i + 1));
                btnCaso.addActionListener(e -> {
                    MisionTresCaso casoSel = casos.get(index);
                    Mission3Result resSel = resultados.get(index);
                    showMission3(casoSel.getGrafo(), casoSel.getOrigen(), casoSel.getDestino(), resSel);
                });
                selectorBar.add(btnCaso);
            }

            selectorBar.setVisible(true);
            topBar.revalidate();
            topBar.repaint();

            MisionTresCaso primerCaso = casos.get(0);
            Mission3Result primerRes = resultados.get(0);
            showMission3(primerCaso.getGrafo(), primerCaso.getOrigen(), primerCaso.getDestino(), primerRes);
        }

        private List<Integer> computeDijkstraSettleOrder(Graph graph, int origen, int destino) {
            List<Integer> order = new ArrayList<>();
            int n = graph.getContadorNodos();
            if (n == 0) return order;
            long[] dist = new long[n];
            Arrays.fill(dist, Long.MAX_VALUE);
            dist[origen] = 0;
            boolean[] settled = new boolean[n];
            PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[1], b[1]));
            pq.add(new long[]{origen, 0});

            while (!pq.isEmpty()) {
                long[] cur = pq.poll();
                int u = (int) cur[0];
                long d = cur[1];
                if (d > dist[u] || settled[u]) continue;
                settled[u] = true;
                order.add(u);
                if (u == destino) break;
                for (Edge edge : graph.getNeightbors(u)) {
                    int v = edge.getPara();
                    long peso = edge.getWeight();
                    if (dist[u] != Long.MAX_VALUE && dist[u] + peso < dist[v]) {
                        dist[v] = dist[u] + peso;
                        pq.add(new long[]{v, dist[v]});
                    }
                }
            }
            return order;
        }

        private JComponent animatedDijkstraCanvas(Graph graph, int origen, int destino, List<Integer> order,
                                                  int[] reveal, Set<Integer> pathNodes, Set<String> pathEdges) {
            JPanel canvas = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int n = graph.getContadorNodos();
                    if (n == 0) {
                        g2.dispose();
                        return;
                    }
                    boolean finished = reveal[0] >= order.size();
                    Set<Integer> settledSoFar = new HashSet<>(order.subList(0, Math.min(reveal[0], order.size())));

                    Point[] points = customGraphLayout(n, getWidth(), getHeight());

                    for (WeightedEdge edge : graph.getEdges()) {
                        int from = edge.getFrom();
                        int to = edge.getTo();
                        boolean onPath = finished && (pathEdges.contains(from + "->" + to) || pathEdges.contains(to + "->" + from));

                        Point p1 = points[from];
                        Point p2 = points[to];

                        if (onPath) {
                            g2.setColor(CAT_GREEN);
                            g2.setStroke(new BasicStroke(2.8f));
                        } else {
                            g2.setColor(new Color(150, 150, 150));
                            Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{6.0f}, 0.0f);
                            g2.setStroke(dashed);
                        }
                        g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));

                        // Weight label
                        int midX = (p1.x + p2.x) / 2;
                        int midY = (p1.y + p2.y) / 2;
                        g2.setColor(WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        g2.drawString(String.valueOf(edge.getWeight()), midX + (p1.x == p2.x ? 8 : -10), midY);
                    }

                    if (graph.getEdges().isEmpty()) {
                        g2.setColor(MUTED);
                        g2.setFont(FONT_SUBTITLE);
                        g2.drawString("no edges at all", getWidth() / 2 - 40, getHeight() / 2);
                    }

                    for (int i = 0; i < n; i++) {
                        Point p = points[i];
                        Color color = new Color(70, 70, 70);
                        if (settledSoFar.contains(i)) {
                            color = new Color(90, 150, 210);
                        }
                        if (finished && pathNodes.contains(i)) {
                            color = ORANGE_SOFT;
                        }
                        if (i == origen) color = CAT_GREEN;
                        if (i == destino) color = ORANGE;

                        g2.setColor(color);
                        g2.fill(new Ellipse2D.Double(p.x - 16, p.y - 16, 32, 32));
                        g2.setColor(CARD_BORDER);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.draw(new Ellipse2D.Double(p.x - 16, p.y - 16, 32, 32));

                        g2.setColor(WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        g2.drawString(String.valueOf(i), p.x - 4, p.y + 5);

                        // Labels start / dest
                        if (i == origen) {
                            g2.setColor(WHITE);
                            g2.setFont(FONT_SUBTITLE);
                            g2.drawString("start", p.x - 12, p.y - 20);
                        }
                        if (i == destino) {
                            g2.setColor(WHITE);
                            g2.setFont(FONT_SUBTITLE);
                            g2.drawString("dest", p.x - 10, p.y + 32);
                        }
                    }
                    g2.dispose();
                }
            };
            canvas.setOpaque(false);
            return canvas;
        }

        private Set<String> consecutiveEdgeKeys(List<Integer> path, boolean directedOnly) {
            Set<String> keys = new HashSet<>();
            if (path == null) return keys;
            for (int i = 0; i + 1 < path.size(); i++) {
                keys.add(path.get(i) + "->" + path.get(i + 1));
                if (!directedOnly) keys.add(path.get(i + 1) + "->" + path.get(i));
            }
            return keys;
        }

        private Point[] customGraphLayout(int n, int width, int height) {
            Point[] points = new Point[n];
            int cx = width / 2;
            int cy = height / 2;

            if (n == 2) {
                points[0] = new Point(cx, cy - 60);
                points[1] = new Point(cx, cy + 60);
            } else if (n == 3) {
                points[2] = new Point(cx, cy - 70);
                points[1] = new Point(cx - 65, cy + 45);
                points[0] = new Point(cx + 65, cy + 45);
            } else {
                int radius = Math.min(width, height) / 2 - 35;
                for (int i = 0; i < n; i++) {
                    double angle = Math.PI * 2 * i / n - Math.PI / 2;
                    points[i] = new Point((int) (cx + Math.cos(angle) * radius), (int) (cy + Math.sin(angle) * radius));
                }
            }
            return points;
        }

        void showMission3(Graph graph, int origen, int destino, Mission3Result result) {
            stopAnimation();
            contentGrid.removeAll();

            BfTrace trace = computeBellmanFordTrace(graph, origen);
            int[] round = {0};

            Set<Integer> finalNodes = new HashSet<>();
            Set<String> finalEdges = new HashSet<>();
            if (result.getEstado() == Mission3Result.Estado.VALOR) {
                List<Integer> path = reconstructPath(trace.previo, origen, destino);
                finalNodes.addAll(path);
                finalEdges.addAll(consecutiveEdgeKeys(path, true));
            } else if (result.getEstado() == Mission3Result.Estado.INFINITO) {
                BellmanFordResult bf = BellmanFord.resolver(graph, origen);
                boolean[] noAcotado = bf.getNoAcotado();
                for (int i = 0; i < noAcotado.length; i++) {
                    if (noAcotado[i]) finalNodes.add(i);
                }
            }


            contentGrid.add(createGraphCard("Grafo", graph, Collections.emptySet(), Collections.emptySet(), origen, destino));
            JComponent canvas = animatedBellmanFordCanvas(graph, origen, destino, trace, round, finalNodes, finalEdges,
                    result.getEstado() == Mission3Result.Estado.INFINITO);
            contentGrid.add(wrapCard("Bellman-Ford (rondas)", canvas));
            contentGrid.add(createGraphCard("Resultado", graph, finalNodes, finalEdges, origen, destino));

            controlsBar.setVisible(true);
            showContent();

            int totalRounds = Math.max(trace.rounds.size(), 1);
            Runnable tick = () -> {
                if (round[0] < trace.rounds.size()) round[0]++;
                canvas.repaint();
                stepLabel.setText("Ronda " + round[0] + "/" + totalRounds);
            };
            Runnable reset = () -> {
                round[0] = 0;
                canvas.repaint();
            };
            BooleanSupplier finished = () -> round[0] >= trace.rounds.size();
            startAnimationEngine(tick, reset, finished);
        }

        private static final class BfTrace {
            List<List<WeightedEdge>> rounds;
            int[] previo;
        }

        private BfTrace computeBellmanFordTrace(Graph grafo, int origen) {
            int n = grafo.getContadorNodos();
            long[] dist = new long[n];
            int[] previo = new int[n];
            Arrays.fill(dist, FloydWarshall.NO_ROUTE);
            Arrays.fill(previo, -1);
            if (n > 0) dist[origen] = 0;
            List<WeightedEdge> aristas = grafo.getEdges();
            List<List<WeightedEdge>> rounds = new ArrayList<>();

            for (int ronda = 1; ronda <= Math.max(n - 1, 0); ronda++) {
                List<WeightedEdge> mejoradas = new ArrayList<>();
                boolean hubo = false;
                for (WeightedEdge arista : aristas) {
                    int u = arista.getFrom();
                    int v = arista.getTo();
                    long w = arista.getWeight();
                    if (dist[u] == FloydWarshall.NO_ROUTE) continue;
                    long candidato = dist[u] + w;
                    if (candidato > dist[v]) {
                        dist[v] = candidato;
                        previo[v] = u;
                        mejoradas.add(arista);
                        hubo = true;
                    }
                }
                rounds.add(mejoradas);
                if (!hubo) break;
            }

            BfTrace trace = new BfTrace();
            trace.rounds = rounds;
            trace.previo = previo;
            return trace;
        }

        private List<Integer> reconstructPath(int[] previo, int origen, int destino) {
            List<Integer> path = new ArrayList<>();
            int at = destino;
            int guard = previo.length + 1;
            while (at != -1 && guard-- > 0) {
                path.add(at);
                if (at == origen) break;
                at = previo[at];
            }
            Collections.reverse(path);
            return path;
        }

        private JComponent animatedBellmanFordCanvas(Graph graph, int origen, int destino, BfTrace trace, int[] round,
                                                     Set<Integer> finalNodes, Set<String> finalEdges, boolean infinite) {
            JPanel canvas = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int n = graph.getContadorNodos();
                    if (n == 0) {
                        g2.dispose();
                        return;
                    }
                    boolean finished = round[0] >= trace.rounds.size();
                    Set<String> activeEdges = new HashSet<>();
                    Set<Integer> reachedNodes = new HashSet<>();
                    reachedNodes.add(origen);
                    for (int r = 0; r < round[0] && r < trace.rounds.size(); r++) {
                        for (WeightedEdge e : trace.rounds.get(r)) {
                            activeEdges.add(e.getFrom() + "->" + e.getTo());
                            reachedNodes.add(e.getFrom());
                            reachedNodes.add(e.getTo());
                        }
                    }

                    Point[] points = customGraphLayout(n, getWidth(), getHeight());
                    Color finalColor = infinite ? new Color(200, 90, 60) : CAT_GREEN;

                    for (WeightedEdge edge : graph.getEdges()) {
                        String key = edge.getFrom() + "->" + edge.getTo();
                        boolean isFinal = finished && finalEdges.contains(key);
                        boolean isActive = !finished && activeEdges.contains(key);

                        g2.setColor(isFinal ? finalColor : isActive ? ORANGE_SOFT : new Color(150, 150, 150));
                        g2.setStroke(new BasicStroke(isFinal || isActive ? 2.6f : 1.2f));
                        drawDirectedEdge(g2, points[edge.getFrom()], points[edge.getTo()]);

                        Point p1 = points[edge.getFrom()];
                        Point p2 = points[edge.getTo()];
                        int midX = (p1.x + p2.x) / 2;
                        int midY = (p1.y + p2.y) / 2;
                        g2.setColor(WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        g2.drawString(String.valueOf(edge.getWeight()), midX, midY);
                    }

                    for (int i = 0; i < n; i++) {
                        Point p = points[i];
                        Color color = new Color(70, 70, 70);
                        if (reachedNodes.contains(i)) color = new Color(90, 150, 210);
                        if (finished && finalNodes.contains(i)) color = finalColor;
                        if (i == origen) color = CAT_GREEN;
                        if (i == destino) color = ORANGE;

                        g2.setColor(color);
                        g2.fill(new Ellipse2D.Double(p.x - 16, p.y - 16, 32, 32));
                        g2.setColor(WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        g2.drawString(String.valueOf(i), p.x - 4, p.y + 5);

                        if (i == origen) {
                            g2.setColor(WHITE);
                            g2.setFont(FONT_SUBTITLE);
                            g2.drawString("start", p.x - 12, p.y - 20);
                        }
                        if (i == destino) {
                            g2.setColor(WHITE);
                            g2.setFont(FONT_SUBTITLE);
                            g2.drawString("dest", p.x - 10, p.y + 32);
                        }
                    }
                    g2.dispose();
                }
            };
            canvas.setOpaque(false);
            return canvas;
        }

        private void drawDirectedEdge(Graphics2D g2, Point from, Point to) {
            g2.draw(new Line2D.Double(from.x, from.y, to.x, to.y));
            double angle = Math.atan2(to.y - from.y, to.x - from.x);
            double arrowX = to.x - 18 * Math.cos(angle);
            double arrowY = to.y - 18 * Math.sin(angle);
            int len = 8;
            int x1 = (int) (arrowX - len * Math.cos(angle - Math.PI / 7));
            int y1 = (int) (arrowY - len * Math.sin(angle - Math.PI / 7));
            int x2 = (int) (arrowX - len * Math.cos(angle + Math.PI / 7));
            int y2 = (int) (arrowY - len * Math.sin(angle + Math.PI / 7));
            g2.fillPolygon(new int[]{(int) arrowX, x1, x2}, new int[]{(int) arrowY, y1, y2}, 3);
        }

        void showMission4(Graph graph, Kruskal.Resultado resultado) throws ENumeroNegativo {
            stopAnimation();
            contentGrid.removeAll();

            List<KruskalStep> steps = computeKruskalSteps(graph);
            int[] reveal = {0};
            Set<String> finalEdges = edgeSet(resultado.getCablesSeleccionados());

            contentGrid.add(createGraphCard("Red", graph, Collections.emptySet(), Collections.emptySet(), -1, -1));
            JComponent canvas = animatedKruskalCanvas(graph, steps, reveal, finalEdges);
            contentGrid.add(wrapCard("Kruskal (evaluando cables)", canvas));
            contentGrid.add(createGraphCard("Resultado", graph, Collections.emptySet(), finalEdges, -1, -1));

            controlsBar.setVisible(true);
            showContent();

            Runnable tick = () -> {
                if (reveal[0] < steps.size()) reveal[0]++;
                canvas.repaint();
                KruskalStep last = reveal[0] > 0 ? steps.get(reveal[0] - 1) : null;
                String verdict = last == null ? "" : (last.accepted ? " (aceptado)" : " (rechazado, forma ciclo)");
                stepLabel.setText("Cable " + reveal[0] + "/" + steps.size() + verdict);
            };
            Runnable reset = () -> {
                reveal[0] = 0;
                canvas.repaint();
            };
            BooleanSupplier finished = () -> reveal[0] >= steps.size();
            startAnimationEngine(tick, reset, finished);
        }

        private static final class KruskalStep {
            final WeightedEdge edge;
            final boolean accepted;

            KruskalStep(WeightedEdge edge, boolean accepted) {
                this.edge = edge;
                this.accepted = accepted;
            }
        }

        private List<KruskalStep> computeKruskalSteps(Graph grafo) throws ENumeroNegativo {
            List<WeightedEdge> cables = new ArrayList<>(grafo.getEdges());
            Collections.sort(cables);
            int n = grafo.getContadorNodos();
            Union conjuntos = new Union(n);
            List<KruskalStep> steps = new ArrayList<>();
            int aceptados = 0;
            for (WeightedEdge cable : cables) {
                if (aceptados == n - 1) break;
                boolean ok = conjuntos.unir(cable.getFrom(), cable.getTo());
                steps.add(new KruskalStep(cable, ok));
                if (ok) aceptados++;
            }
            return steps;
        }

        private JComponent animatedKruskalCanvas(Graph graph, List<KruskalStep> steps, int[] reveal, Set<String> finalEdges) {
            JPanel canvas = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int n = graph.getContadorNodos();
                    if (n == 0) {
                        g2.dispose();
                        return;
                    }
                    Map<WeightedEdge, Boolean> decisions = new HashMap<>();
                    for (int i = 0; i < reveal[0] && i < steps.size(); i++) {
                        decisions.put(steps.get(i).edge, steps.get(i).accepted);
                    }
                    WeightedEdge current = reveal[0] > 0 && reveal[0] <= steps.size()
                            ? steps.get(reveal[0] - 1).edge : null;

                    Point[] points = customGraphLayout(n, getWidth(), getHeight());

                    for (WeightedEdge edge : graph.getEdges()) {
                        Boolean decision = decisions.get(edge);
                        boolean isCurrent = edge == current;
                        Stroke dashed = new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                                4, new float[]{5, 5}, 0);
                        if (decision == null) {
                            g2.setColor(new Color(120, 120, 120));
                            g2.setStroke(new BasicStroke(1.2f));
                        } else if (decision) {
                            g2.setColor(CAT_GREEN);
                            g2.setStroke(new BasicStroke(2.8f));
                        } else {
                            g2.setColor(new Color(160, 70, 70));
                            g2.setStroke(dashed);
                        }
                        if (isCurrent) {
                            g2.setColor(ORANGE_SOFT);
                            g2.setStroke(new BasicStroke(3.4f));
                        }
                        g2.draw(new Line2D.Double(points[edge.getFrom()].x, points[edge.getFrom()].y,
                                points[edge.getTo()].x, points[edge.getTo()].y));

                        Point p1 = points[edge.getFrom()];
                        Point p2 = points[edge.getTo()];
                        int midX = (p1.x + p2.x) / 2;
                        int midY = (p1.y + p2.y) / 2;
                        g2.setColor(WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        g2.drawString(String.valueOf(edge.getWeight()), midX, midY);
                    }

                    for (int i = 0; i < n; i++) {
                        Point p = points[i];
                        g2.setColor(new Color(70, 70, 70));
                        g2.fill(new Ellipse2D.Double(p.x - 16, p.y - 16, 32, 32));
                        g2.setColor(WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        g2.drawString(String.valueOf(i), p.x - 4, p.y + 5);
                    }
                    g2.dispose();
                }
            };
            canvas.setOpaque(false);
            return canvas;
        }

        private void showContent() {
            previewCardLayout.show(previewCards, "content");
            contentGrid.revalidate();
            contentGrid.repaint();
        }

        private JPanel wrapCard(String title, JComponent content) {
            JPanel card = new JPanel(new BorderLayout());
            card.setOpaque(false);
            card.setBorder(new LineBorder(CARD_BORDER, 1, true));

            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setForeground(WHITE);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setOpaque(true);
            titleLabel.setBackground(PANEL_LIGHT);
            titleLabel.setBorder(new EmptyBorder(5, 6, 5, 6));

            card.add(titleLabel, BorderLayout.NORTH);
            card.add(content, BorderLayout.CENTER);
            return card;
        }

        private JPanel createGridCard(String title, Grid grid, Punto inicio, Punto destino, List<Punto> highlightPath) {
            JComponent canvas = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int rows = grid.getFilas();
                    int cols = grid.getColumnas();
                    int cell = Math.min((getWidth() - 20) / Math.max(cols, 1), (getHeight() - 20) / Math.max(rows, 1));
                    int offsetX = (getWidth() - cols * cell) / 2;
                    int offsetY = (getHeight() - rows * cell) / 2;

                    Set<String> cells = new HashSet<>();
                    if (highlightPath != null) {
                        for (Punto punto : highlightPath) {
                            cells.add(punto.getFila() + "," + punto.getColumna());
                        }
                    }

                    for (int r = 0; r < rows; r++) {
                        for (int c = 0; c < cols; c++) {
                            int x = offsetX + c * cell;
                            int y = offsetY + r * cell;
                            Color color = new Color(50, 50, 50);
                            if (highlightPath != null && cells.contains(r + "," + c)) {
                                color = ORANGE;
                            }
                            if (grid.hayBomba(r, c)) {
                                color = CAT_RED;
                            }
                            if (inicio != null && r == inicio.getFila() && c == inicio.getColumna()) {
                                color = CAT_GREEN;
                            }
                            if (destino != null && r == destino.getFila() && c == destino.getColumna()) {
                                color = ORANGE_SOFT;
                            }
                            g2.setColor(color);
                            g2.fillRect(x, y, cell, cell);
                            g2.setColor(new Color(28, 28, 28));
                            g2.drawRect(x, y, cell, cell);
                        }
                    }
                    g2.dispose();
                }
            };
            canvas.setOpaque(false);
            return wrapCard(title, canvas);
        }

        private JPanel createGraphCard(String title, Graph graph, Set<Integer> highlightedNodes,
                                       Set<String> highlightedEdges, int origen, int destino) {
            JComponent canvas = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int n = graph.getContadorNodos();
                    if (n == 0) {
                        g2.dispose();
                        return;
                    }

                    Point[] points = customGraphLayout(n, getWidth(), getHeight());

                    for (WeightedEdge edge : graph.getEdges()) {
                        int from = edge.getFrom();
                        int to = edge.getTo();
                        boolean highlight = highlightedEdges.contains(from + "->" + to)
                                || highlightedEdges.contains(to + "->" + from);

                        Point p1 = points[from];
                        Point p2 = points[to];

                        if (highlight) {
                            g2.setColor(CAT_GREEN);
                            g2.setStroke(new BasicStroke(2.8f));
                        } else {
                            g2.setColor(new Color(150, 150, 150));
                            Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{6.0f}, 0.0f);
                            g2.setStroke(dashed);
                        }
                        g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));

                        int midX = (p1.x + p2.x) / 2;
                        int midY = (p1.y + p2.y) / 2;
                        g2.setColor(WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        g2.drawString(String.valueOf(edge.getWeight()), midX, midY);
                    }

                    if (graph.getEdges().isEmpty()) {
                        g2.setColor(MUTED);
                        g2.setFont(FONT_SUBTITLE);
                        g2.drawString("no edges at all", getWidth() / 2 - 40, getHeight() / 2);
                    }

                    for (int i = 0; i < n; i++) {
                        Point p = points[i];
                        g2.setColor(highlightedNodes.contains(i) ? ORANGE_SOFT : new Color(70, 70, 70));
                        if (i == origen || i == destino) {
                            g2.setColor(i == origen ? CAT_GREEN : ORANGE);
                        }

                        g2.fill(new Ellipse2D.Double(p.x - 16, p.y - 16, 32, 32));
                        g2.setColor(WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        g2.drawString(String.valueOf(i), p.x - 4, p.y + 5);

                        if (i == origen && origen >= 0) {
                            g2.setColor(WHITE);
                            g2.setFont(FONT_SUBTITLE);
                            g2.drawString("start", p.x - 12, p.y - 20);
                        }
                        if (i == destino && destino >= 0) {
                            g2.setColor(WHITE);
                            g2.setFont(FONT_SUBTITLE);
                            g2.drawString("dest", p.x - 10, p.y + 32);
                        }
                    }
                    g2.dispose();
                }
            };
            canvas.setOpaque(false);
            return wrapCard(title, canvas);
        }

        private Set<Integer> pathNodes(List<Integer> camino) {
            Set<Integer> set = new HashSet<>();
            if (camino != null) {
                for (Integer node : camino) {
                    set.add(node);
                }
            }
            return set;
        }

        private Set<String> edgeSet(List<WeightedEdge> edges) {
            Set<String> set = new HashSet<>();
            if (edges != null) {
                for (WeightedEdge edge : edges) {
                    set.add(edge.getFrom() + "->" + edge.getTo());
                }
            }
            return set;
        }
    }
}