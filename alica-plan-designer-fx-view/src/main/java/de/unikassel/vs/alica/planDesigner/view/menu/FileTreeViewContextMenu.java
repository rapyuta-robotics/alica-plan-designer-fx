package de.unikassel.vs.alica.planDesigner.view.menu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;

import java.io.File;

public class FileTreeViewContextMenu extends ContextMenu {

    private File hintFile;
    private DeleteFileMenuItem deleteFileMenuItem;
    private RenameFileMenuItem renameFileMenuItem;
    private NewResourceMenu newResourceMenu;
    private CopyFileMenuItem copyFileMenuItem;

    private TreeCell treeCell;

    public FileTreeViewContextMenu() {
        deleteFileMenuItem = new DeleteFileMenuItem();
        renameFileMenuItem = new RenameFileMenuItem();
        newResourceMenu = new NewResourceMenu(hintFile);
        copyFileMenuItem = new CopyFileMenuItem();
        getItems().addAll(newResourceMenu, copyFileMenuItem, renameFileMenuItem, deleteFileMenuItem);
    }

    public void setHintFile(File hintFile) {
        this.hintFile = hintFile;

        // Copy isDisable for Folders
        if(hintFile.isDirectory()){
            copyFileMenuItem.setDisable(true);
        } else {
            copyFileMenuItem.setDisable(false);
        }

        newResourceMenu.setInitialDirectoryHint(hintFile);
    }

    public void showRoleSetItem(boolean show) { newResourceMenu.showRoleSetItem(show); }

    public void showTaskrepositoryItem(boolean show) {
        newResourceMenu.showTaskRepositoryItem(show);
    }

    public void setTreeCell(TreeCell treeCell) {
        this.treeCell = treeCell;
        renameFileMenuItem.setTreeCell(treeCell);
        deleteFileMenuItem.setTreeCell(treeCell);
        copyFileMenuItem.setTreeCell(treeCell);
    }

    public NewResourceMenu getNewResourceMenu() {
        return newResourceMenu;
    }
}
