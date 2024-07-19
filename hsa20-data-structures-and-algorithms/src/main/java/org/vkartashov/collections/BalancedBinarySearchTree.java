package org.vkartashov.collections;

import java.util.ArrayList;
import java.util.List;

public class BalancedBinarySearchTree<T extends Comparable<T>> {

    private int size = 0;

    private Node<T> root;

    private static class Node<T> {
        private T data;
        private Node<T> left;
        private Node<T> right;

        public Node(T data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    public BalancedBinarySearchTree() {
        this.root = null;
    }

    public void insert(T data) {
        this.root = insert(this.root, data);
        size++;
    }

    private Node<T> insert(Node<T> node, T data) {
        if (node == null) {
            return new Node<>(data);
        }

        if (data.compareTo(node.data) < 0) {
            node.left = insert(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = insert(node.right, data);
        }

        return node;
    }

    public T find(T data) {
        Node<T> node = find(this.root, data);
        return node == null ? null : node.data;
    }

    public boolean contains(T data) {
        return find(data) != null;
    }

    private Node<T> find(Node<T> node, T data) {
        try {
            Thread.sleep(100);
//            System.out.println(size + "Searching in node " + node.data + "L: " + (node.left == null ? "null" : node.left.data) + "R: " + (node.right == null ? "null" : node.right.data));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (node == null) {
            return null;
        }

        if (data.compareTo(node.data) < 0) {
            return find(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            return find(node.right, data);
        } else {
            return node;
        }
    }

    public void delete(T data) {
        this.root = delete(this.root, data);
        size--;
    }

    private Node<T> delete(Node<T> node, T data) {
        if (node == null) {
            return null;
        }

        if (data.compareTo(node.data) < 0) {
            node.left = delete(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = delete(node.right, data);
        } else {
            // If the node has no children, simply return null.
            if (node.left == null && node.right == null) {
                return null;
            }

            // If the node has only one child, return that child.
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }

            // If the node has two children, find the smallest element in the right subtree and replace the
            // current node with it.
            T smallestRightSubtreeElement = findSmallest(node.right);
            node.data = smallestRightSubtreeElement;
            node.right = delete(node.right, smallestRightSubtreeElement);
        }

        return node;
    }

    private T findSmallest(Node<T> node) {
        if (node.left == null) {
            return node.data;
        } else {
            return findSmallest(node.left);
        }
    }

    public void balance() {
        this.root = balance(this.root);
    }

    private Node<T> balance(Node<T> root) {
        if (root == null) {
            return null;
        }

        // Inorder traversal of the BST.
        List<T> sortedList = new ArrayList<>();
        inorderTraversal(root, sortedList);

        // Build a balanced BST from the sorted list.
        return buildBalancedBST(sortedList, 0, sortedList.size() - 1);
    }

    private void inorderTraversal(Node<T> root, List<T> sortedList) {
        if (root == null) {
            return;
        }

        inorderTraversal(root.left, sortedList);
        sortedList.add(root.data);
        inorderTraversal(root.right, sortedList);
    }

    private Node<T> buildBalancedBST(List<T> sortedList, int start, int end) {
        if (start > end) {
            return null;
        }

        int mid = (start + end) / 2;
        Node<T> root = new Node<T>(sortedList.get(mid));

        root.left = buildBalancedBST(sortedList, start, mid - 1);
        root.right = buildBalancedBST(sortedList, mid + 1, end);

        return root;
    }

    private int height(Node<T> node) {
        if (node == null) {
            return 0;
        }

        return Math.max(height(node.left), height(node.right)) + 1;
    }

    public boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isBalanced(Node<T> node) {
        if (node == null) {
            return true;
        }

        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        if (Math.abs(leftHeight - rightHeight) > 1) {
            return false;
        }

        return isBalanced(node.left) && isBalanced(node.right);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

}
