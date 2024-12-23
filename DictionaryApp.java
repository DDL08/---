import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class DictionaryApp {

    // AVL Node class
    static class AVLNode {
        String word;
        String translation;
        AVLNode left, right;
        int height;

        public AVLNode(String word, String translation) {
            this.word = word;
            this.translation = translation;
            this.left = this.right = null;
            this.height = 1;
        }
    }

    // AVL Tree class
    static class AVLTree {
        private AVLNode root;

        public AVLTree() {
            root = null;
        }

        public int getHeight(AVLNode node) {
            return (node == null) ? 0 : node.height;
        }

        public void updateHeight(AVLNode node) {
            node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        }

        public int getBalance(AVLNode node) {
            return (node == null) ? 0 : getHeight(node.left) - getHeight(node.right);
        }

        public AVLNode rightRotate(AVLNode y) {
            AVLNode x = y.left;
            AVLNode T2 = x.right;
            x.right = y;
            y.left = T2;
            updateHeight(y);
            updateHeight(x);
            return x;
        }

        public AVLNode leftRotate(AVLNode x) {
            AVLNode y = x.right;
            AVLNode T2 = y.left;
            y.left = x;
            x.right = T2;
            updateHeight(x);
            updateHeight(y);
            return y;
        }

        public AVLNode insert(AVLNode node, String word, String translation) {
            if (node == null) return new AVLNode(word, translation);

            if (word.compareTo(node.word) < 0) {
                node.left = insert(node.left, word, translation);
            } else if (word.compareTo(node.word) > 0) {
                node.right = insert(node.right, word, translation);
            } else {
                return node;  // Duplicate word not allowed
            }

            updateHeight(node);
            int balance = getBalance(node);

            // Balancing the tree
            if (balance > 1 && word.compareTo(node.left.word) < 0) {
                return rightRotate(node);
            }
            if (balance < -1 && word.compareTo(node.right.word) > 0) {
                return leftRotate(node);
            }
            if (balance > 1 && word.compareTo(node.left.word) > 0) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
            if (balance < -1 && word.compareTo(node.right.word) < 0) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }

            return node;
        }

        public AVLNode search(AVLNode node, String word) {
            if (node == null || node.word.equals(word)) {
                return node;
            }
            if (word.compareTo(node.word) < 0) {
                return search(node.left, word);
            } else {
                return search(node.right, word);
            }
        }

        public AVLNode delete(AVLNode node, String word) {
            if (node == null) {
                return node;
            }

            if (word.compareTo(node.word) < 0) {
                node.left = delete(node.left, word);
            } else if (word.compareTo(node.word) > 0) {
                node.right = delete(node.right, word);
            } else {
                if (node.left == null) {
                    return node.right;
                } else if (node.right == null) {
                    return node.left;
                }

                AVLNode minNode = getMinValueNode(node.right);
                node.word = minNode.word;
                node.translation = minNode.translation;
                node.right = delete(node.right, minNode.word);
            }

            updateHeight(node);
            int balance = getBalance(node);

            if (balance > 1 && getBalance(node.left) >= 0) {
                return rightRotate(node);
            }

            if (balance < -1 && getBalance(node.right) <= 0) {
                return leftRotate(node);
            }

            if (balance > 1 && getBalance(node.left) < 0) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }

            if (balance < -1 && getBalance(node.right) > 0) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }

            return node;
        }

        private AVLNode getMinValueNode(AVLNode node) {
            if (node == null || node.left == null) {
                return node;
            }
            return getMinValueNode(node.left);
        }

        public void saveTreeToFiles(String directory) {
            for (char letter = 'a'; letter <= 'z'; letter++) {
                String filePath = directory + "/" + letter + ".txt";
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"))) {
                    saveTreeToFile(root, writer, letter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void saveTreeToFile(AVLNode node, BufferedWriter writer, char letter) throws IOException {
            if (node != null) {
                if (node.word.charAt(0) == letter) {
                    writer.write(node.word + "\n" + node.translation + "\n");
                }
                saveTreeToFile(node.left, writer, letter);
                saveTreeToFile(node.right, writer, letter);
            }
        }

        public AVLNode getRoot() {
            return root;
        }

        public void setRoot(AVLNode root) {
            this.root = root;
        }
    }

    // DictionaryApp GUI
    static class DictionaryAppGUI {
        private JFrame frame;
        private JTextField wordField, translationField;
        private AVLTree tree;

        public DictionaryAppGUI(AVLTree tree) {
            this.tree = tree;
            frame = new JFrame("English-Chinese Dictionary");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());

            JLabel wordLabel = new JLabel("Enter Word:");
            wordField = new JTextField(20);
            JLabel translationLabel = new JLabel("Enter Translation:");
            translationField = new JTextField(20);

            JButton searchButton = new JButton("Search");
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchWord();
                }
            });

            JButton insertButton = new JButton("Insert");
            insertButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertWord();
                }
            });

            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteWord();
                }
            });

            frame.add(wordLabel);
            frame.add(wordField);
            frame.add(translationLabel);
            frame.add(translationField);
            frame.add(searchButton);
            frame.add(insertButton);
            frame.add(deleteButton);
        }

        public void show() {
            frame.setVisible(true);
        }

        private void searchWord() {
            String word = wordField.getText().trim();
            AVLNode result = tree.search(tree.getRoot(), word);
            if (result != null) {
                JOptionPane.showMessageDialog(frame, "Word: " + result.word + "\nTranslation: " + result.translation);
            } else {
                JOptionPane.showMessageDialog(frame, "Word not found!", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        }

        private void insertWord() {
            String word = wordField.getText().trim();
            String translation = translationField.getText().trim();
            if (word.isEmpty() || translation.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both word and translation.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            tree.setRoot(tree.insert(tree.getRoot(), word, translation));
            tree.saveTreeToFiles("C:/Users/LEGION/Desktop/新建文件夹/02/src/java-code1/word_source");
            JOptionPane.showMessageDialog(frame, "Word '" + word + "' inserted!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        private void deleteWord() {
            String word = wordField.getText().trim();
            if (word.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a word to delete.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            AVLNode result = tree.search(tree.getRoot(), word);
            if (result != null) {
                tree.setRoot(tree.delete(tree.getRoot(), word));
                tree.saveTreeToFiles("C:/Users/LEGION/Desktop/新建文件夹/02/src/java-code1/word_source");
                JOptionPane.showMessageDialog(frame, "Word '" + word + "' deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Word not found!", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Load words from files
    public static AVLTree loadWordsFromFiles(String directory) {
        AVLTree tree = new AVLTree();
        for (char letter = 'a'; letter <= 'z'; letter++) {
            String filePath = directory + "/" + letter + ".txt";
            File file = new File(filePath);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                    String word;
                    while ((word = reader.readLine()) != null) {
                        String translation = reader.readLine();
                        if (translation == null) translation = "";
                        tree.setRoot(tree.insert(tree.getRoot(), word, translation));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tree;
    }

    public static void main(String[] args) {
        AVLTree tree = loadWordsFromFiles("C:/Users/LEGION/Desktop/新建文件夹/02/src/java-code1/word_source");
        DictionaryAppGUI app = new DictionaryAppGUI(tree);
        app.show();
    }
}
