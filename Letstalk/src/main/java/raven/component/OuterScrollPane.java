// package raven.component;

// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ComponentAdapter;
// import java.awt.event.ComponentEvent;

// public class OuterScrollPane extends JScrollPane {

//     private PostPanel postPanel;

//     public OuterScrollPane(PostPanel postPanel) {
//         super(postPanel);
//         this.postPanel = postPanel;

//         setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//         setPreferredSize(new Dimension(400, 200));

//         getViewport().addComponentListener(new ComponentAdapter() {
//             @Override
//             public void componentResized(ComponentEvent e) {
//                 updateInnerScrollPaneSize();
//             }
//         });
//     }

//     private void updateInnerScrollPaneSize() {
//         getViewport().getView().setPreferredSize(new Dimension(getWidth(), postPanel.getHeight()));
//         revalidate();
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             JFrame frame = new JFrame("Post Panel Example");
//             frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//             PostPanel postPanel = new PostPanel("example@email.com");
//             OuterScrollPane outerScrollPane = new OuterScrollPane(postPanel);

//             frame.add(outerScrollPane);
//             frame.pack();
//             frame.setVisible(true);
//         });
//     }
// }

