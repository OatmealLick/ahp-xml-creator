package treemaker;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lick on 08.04.17.
 */
public class CustomTextField extends TextField {

    private HBox children;

    public List<CustomTextField> getChildCustomTextFields() {

        List<CustomTextField> list = new ArrayList<>();

        if(children == null)
            return list;

        for (Node child : children.getChildren()) {
            list.add((CustomTextField) child);
        }
        return list;
    }

    public HBox getHBox() {
        return children;
    }

    public void setChildren(HBox children) {
        this.children = children;
    }

    public boolean hasAnyChildren() {
        return children != null;
    }

    public boolean hasNoChildren() {
        return !hasAnyChildren();
    }

    public int getChildrenCount() {
        if(children == null)
            return 0;

        return children.getChildren().size();
    }
}
