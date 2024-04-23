package byteback.specification.model;

public class Box<T> {

    private volatile T content;

    public void put(final T element) {
        content = element;
    }

    public T read() {
        return content;
    }

}
