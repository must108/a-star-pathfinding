import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;


public class DemoPanel extends JPanel {
    final int maxCol = 60;
    final int maxRow = 40;
    final int nodeSize = 20;
    final int screenWidth = nodeSize * maxCol;
    final int screenHeight = nodeSize * maxRow;
    Random rand = new Random();

    Node[][] node = new Node[maxCol][maxRow];
    Node startNode, goalNode, currentNode;
    ArrayList<Node> openList = new ArrayList<>();
    ArrayList<Node> checkedList = new ArrayList<>();

    boolean goalReached = false;
    int step = 0;

    public DemoPanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setLayout(new GridLayout(maxRow, maxCol));
        this.addKeyListener(new KeyHandler(this));
        this.setFocusable(true);

        int col = 0;
        int row = 0;

        while(col < maxCol && row < maxRow) {
            node[col][row] = new Node(col, row);
            this.add(node[col][row]);

            col += 1;
            if(col == maxCol) {
                col = 0;
                row += 1;
            }
        }    

        setStartNode(0, 0);
        setGoalNode(maxCol - 1, maxRow - 1);

        for(int i = 0; i < 800; i++) {
            int col1 = rand.nextInt(maxCol);
            int row1 = rand.nextInt(maxRow);

            if((col1 == 0 && row1 == 0) || (col1 == maxCol - 1 && row1 == maxRow - 1)) {
                continue;
            } else {
                setSolidNode(col1, row1);
            }
            
        }

        setCostOnNodes();
    }

    private void setStartNode(int col, int row) {
        node[col][row].setAsStart();
        startNode = node[col][row];
        currentNode = startNode;
    }

    
    private void setGoalNode(int col, int row) {
        node[col][row].setAsGoal();
        goalNode = node[col][row];
    }

    private void setSolidNode(int col, int row) {
        node[col][row].setAsSolid();
    }

    private void setCostOnNodes() {

        int col = 0;
        int row = 0;

        while(col < maxCol && row < maxRow) {
            getCost(node[col][row]);
            col += 1;
            if(col == maxCol) {
                col = 0;
                row += 1;
            }
        }
    }

    private void getCost(Node node) {

        int xDist = Math.abs(node.col - startNode.col);
        int yDist = Math.abs(node.row - startNode.row);
        node.gCost = xDist + yDist;

        xDist = Math.abs(node.col - goalNode.col);
        yDist = Math.abs(node.row - goalNode.row);
        node.hCost = xDist + yDist;

        node.fCost = node.gCost + node.hCost;
    }

    public void autoSearch() {
        while(goalReached == false && step < 300) {
            int col = currentNode.col;
            int row = currentNode.row;

            currentNode.setAsChecked();
            checkedList.add(currentNode);
            openList.remove(currentNode);

            if(row - 1 >= 0) {
                openNode(node[col][row - 1]);
            }

            if(col - 1 >= 0) {
                openNode(node[col - 1][row]);
            }

            if(row + 1 < maxRow) {
                openNode(node[col][row + 1]);
            }

            if(col + 1 < maxCol) {
                openNode(node[col + 1][row]);
            }

            int bestNodeIndex = 0;
            int bestNodefCost = 999;

            for(int i = 0; i < openList.size(); i++) {
                if(openList.get(i).fCost < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                } else if(openList.get(i).fCost == bestNodefCost) {
                    if(openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }

            currentNode = openList.get(bestNodeIndex);

            if(currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }
        }
        step += 1;
    }

    private void openNode(Node node) {
        if(node.open == false && node.checked == false && node.solid == false) {
            node.setAsOpen();
            node.parent = currentNode;
            openList.add(node);
        }
    }

    private void trackThePath() {
        Node current = goalNode;

        while(current != startNode) {
            current = current.parent;

            if(current != startNode) {
                current.setAsPath();
            }
        }
    }
}
