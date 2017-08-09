package de.uni_kassel.vs.cn.planDesigner.ui.filebrowser;

import de.uni_kassel.vs.cn.planDesigner.common.FileWrapper;
import de.uni_kassel.vs.cn.planDesigner.ui.img.AlicaIcon;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marci on 11.11.16.
 */
public class PLDTreeItem extends TreeItem<FileWrapper> {
    public PLDTreeItem(FileWrapper value, Node graphic) {
        super(value, graphic);
        if (value.unwrap().isDirectory()) {
            for (File content : value.unwrap().listFiles()) {
                Image listItemImage = getImageForFileType(content);
                if (listItemImage == null) {
                    continue;
                }
                getChildren().add(new PLDTreeItem(new FileWrapper(content), new ImageView(listItemImage)));
            }
            getChildren().sort(Comparator.comparing(o -> o.getValue().unwrap().toURI().toString()));
        }
    }

    public void updateDirectory(WatchEvent.Kind kind, Path child) {
        FileWrapper value = getValue();
        List<TreeItem<FileWrapper>> collect = getChildren()
                .stream()
                .filter(e -> child.toFile().getAbsolutePath().contains(e.getValue().unwrap().getAbsolutePath()))
                .collect(Collectors.toList());
        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE) && child.toFile().isDirectory() && collect.size() == 1) {
            ((PLDTreeItem)collect.get(0)).updateDirectory(kind, child);
            return;
        }

        if (collect.size() == 1 && collect.get(0).getChildren().stream().noneMatch(e -> child.toFile().getAbsolutePath().contains(e.getValue().unwrap().getAbsolutePath()))) {
            if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY) || kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                getChildren().remove(collect.get(0));
                if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    for (File content : value.unwrap().listFiles()) {

                        boolean isAlreadyKnownToTreeItem = getChildren().stream().anyMatch(e -> e.getValue().unwrap().equals(content));
                        if (isAlreadyKnownToTreeItem == false) {
                            Image listItemImage = getImageForFileType(content);
                            if (listItemImage == null) {
                                continue;
                            }
                            getChildren().add(new PLDTreeItem(new FileWrapper(content), new ImageView(listItemImage)));
                            getChildren().sort(Comparator.comparing(o -> o.getValue().unwrap().toURI().toString()));
                        }
                    }
                }
            } else if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                for (File content : value.unwrap().listFiles()) {

                    boolean isAlreadyKnownToTreeItem = getChildren().stream().anyMatch(e -> e.getValue().unwrap().equals(content));
                    if (isAlreadyKnownToTreeItem == false) {
                        Image listItemImage = getImageForFileType(content);
                        if (listItemImage == null) {
                            continue;
                        }
                        getChildren().add(new PLDTreeItem(new FileWrapper(content), new ImageView(listItemImage)));
                    }
                }
                getChildren().sort(Comparator.comparing(o -> o.getValue().unwrap().toURI().toString()));
            }
        } else if (child.toFile().isDirectory() && child.toFile().getParentFile().equals(value.unwrap())){
            getChildren().add(new PLDTreeItem(new FileWrapper(child.toFile()), new ImageView(getImageForFileType(child.toFile()))));
            getChildren().sort(Comparator.comparing(o -> o.getValue().unwrap().toURI().toString()));
        } else if(child.toFile().getParentFile().equals(value.unwrap())) {
            getChildren().add(new PLDTreeItem(new FileWrapper(child.toFile()), new ImageView(getImageForFileType(child.toFile()))));
        } else {
            collect.forEach(e -> ((PLDTreeItem)e).updateDirectory(kind, child));
        }
        setExpanded(true);
    }

    private Image getImageForFileType(File content) {
        Image listItemImage = null;
        if (content.getName().endsWith(".beh")) {
            listItemImage = new Image((getClass().getClassLoader().getResourceAsStream("images/behaviour24x24.png")));
        } else if (content.getName().endsWith(".pml")) {
            listItemImage = new Image((getClass().getClassLoader().getResourceAsStream("images/plan24x24.png")));
        } else if (content.getName().endsWith(".pty")) {
            listItemImage = new Image((getClass().getClassLoader().getResourceAsStream("images/planTyp24x24.png")));
        } else if (content.isDirectory() || content.getName().endsWith(".tsk")) {
            listItemImage = new Image((getClass().getClassLoader().getResourceAsStream("images/folder24x24.png")));
        } else if (content.isDirectory()) {
            listItemImage = new Image((getClass().getClassLoader().getResourceAsStream("images/folder24x24.png")));
        } else  {
            return null;
        }
        return listItemImage;
    }
}
