import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public static HandMadeLinkedList history = new HandMadeLinkedList();
    public static HashMap<Integer, HandMadeLinkedList.Node> node = new HashMap<>();


    @Override
    public void addTask(Task task) {
        if (node.containsKey(task.id)) {
            removeTask(task.id);
            node.put(task.id, history.linkLast(task));

        } else {
            node.put(task.id, history.linkLast(task));
        }

    }

    @Override
    public void removeTask(int id) {
        HandMadeLinkedList.Node data = node.get(id);
        node.remove(id);
        removeNode(data);
    }

    private void removeNode(HandMadeLinkedList.Node data) {
        if (data.next == null && data.prev == null) {
            history.setHead(null);
            history.setTail(null);
            return;
        }
        if (data.prev != null) {
            data.prev.next = data.next;
        } else {
            history.setHead(data.next);
        }
        if (data.next != null) {
            data.next.prev = data.prev;
        } else {
            history.setTail(data.prev);
        }


    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    public static class HandMadeLinkedList {

        static class Node {
            public Task data;
            public Node next;
            public Node prev;

            public Node(Node prev, Task data, Node next) {
                this.data = data;
                this.next = next;
                this.prev = prev;
            }
        }


        /**
         * Указатель на первый элемент списка. Он же first
         */
        private Node head;

        /**
         * Указатель на последний элемент списка. Он же last
         */
        private Node tail;


        public Node linkLast(Task element) {
            final Node oldTail = tail;
            final Node newNode = new Node(oldTail, element, null);
            if (oldTail == null) {
                head = newNode;
            } else {
                newNode.prev.next = newNode;
            }
            tail = newNode;

            return newNode;
        }

        public ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            Node tmp = tail;
            while (tmp != null) {
                tasks.add(tmp.data);
                tmp = tmp.prev;
            }
            return tasks;
        }

        public void setHead(Node head) {
            this.head = head;
        }

        public void setTail(Node tail) {
            this.tail = tail;
        }
    }
}
