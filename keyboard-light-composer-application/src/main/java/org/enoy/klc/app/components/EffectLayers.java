package org.enoy.klc.app.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.enoy.klc.app.components.tree.EffectGroupLayerContainer;
import org.enoy.klc.app.components.tree.LayerBaseContainer;
import org.enoy.klc.app.components.tree.LayerBaseTreeCell;
import org.enoy.klc.app.components.utils.DialogUtil;
import org.enoy.klc.app.components.utils.LayerTreeItemListenerUtil;
import org.enoy.klc.common.layers.EffectGroupLayer;
import org.enoy.klc.common.layers.EffectLayer;
import org.enoy.klc.common.layers.LayerBase;

import java.net.URL;
import java.util.ResourceBundle;

public class EffectLayers implements Initializable {

	@FXML
	private TreeView<LayerBaseContainer<? extends LayerBase>> treeViewLayers;

	@FXML
	private ToolBar toolBar;

	private EffectGroupLayer root;

	private TreeItem<LayerBaseContainer<? extends LayerBase>> treeRoot;

	private LayerSelectListener selectListener;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		treeViewLayers.setCellFactory(treeView -> new LayerBaseTreeCell());
		treeViewLayers.setShowRoot(false);
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
			if (n != null) {
				LayerBase layerBase = n.getValue().getLayerBase();
				selectListener.onEffectLayerSelected(layerBase);
			} else {
				selectListener.onEffectLayerSelected(null);
			}
		});

		resetTreeView();
	}

	private MultipleSelectionModel<TreeItem<LayerBaseContainer<? extends LayerBase>>> getSelectionModel() {
		return treeViewLayers.getSelectionModel();
	}

	@FXML
	private void delete(ActionEvent event) {
		// TODO: close property editor!
		int selectedIndex = getSelectionModel().getSelectedIndex();
		DialogUtil.confirm(treeViewLayers.getScene().getWindow(), "Confirmation", null, "Effect Layer will be deleted.",
				this::deleteSelectedTreeItem);
		getSelectionModel().clearAndSelect(selectedIndex);

	}

	private void deleteSelectedTreeItem() {
		TreeItem<LayerBaseContainer<? extends LayerBase>> selectedItem = getSelectedItem();
		selectedItem.getParent().getChildren().remove(selectedItem);
	}

	private TreeItem<LayerBaseContainer<? extends LayerBase>> getSelectedItem() {
		return getSelectionModel().getSelectedItem();
	}

	@FXML
	private void addFolder(ActionEvent event) {
		TreeItem<LayerBaseContainer<? extends LayerBase>> selectedItem = getSelectedItem();
		TreeItem<LayerBaseContainer<? extends LayerBase>> effectGroup = createEffectGroup();

		if (selectedItem != null) {
			LayerBase layerBase = selectedItem.getValue().getLayerBase();
			if (layerBase instanceof EffectLayer) {
				selectedItem.getParent().getChildren().add(effectGroup);
			} else if (layerBase instanceof EffectGroupLayer) {
				selectedItem.getChildren().add(effectGroup);
			}
		} else {
			treeRoot.getChildren().add(effectGroup);
		}

	}

	private void resetTreeView() {

		EffectGroupLayer root = new EffectGroupLayer("Root");
		TreeItem<LayerBaseContainer<?>> treeRoot = new TreeItem<>(new EffectGroupLayerContainer(root));
		setRoot(treeRoot, root);

	}

	private TreeItem<LayerBaseContainer<? extends LayerBase>> createEffectGroup() {
		TreeItem<LayerBaseContainer<? extends LayerBase>> effectGroupTreeItem = new TreeItem<LayerBaseContainer<? extends LayerBase>>(
				new LayerBaseContainer<LayerBase>(new EffectGroupLayer("New Group")));
		LayerTreeItemListenerUtil.addListenersToTreeItem(effectGroupTreeItem);
		return effectGroupTreeItem;
	}

	public void setSelectListener(LayerSelectListener selectListener) {
		this.selectListener = selectListener;
	}

	public EffectGroupLayer getRoot() {
		return root;
	}

	public static interface LayerSelectListener {

		public void onEffectLayerSelected(LayerBase layer);

	}

	public void setRoot(TreeItem<LayerBaseContainer<?>> rootTreeItem) {

		EffectGroupLayer layerBase = (EffectGroupLayer) rootTreeItem.getValue().getLayerBase();
		setRoot(rootTreeItem, layerBase);

	}

	private void setRoot(TreeItem<LayerBaseContainer<?>> rootTreeItem, LayerBase layerBase) {
		if (root != null) {
			root.delete();
		}

		// TODO: exception
		root = (EffectGroupLayer) layerBase;
		treeRoot = rootTreeItem;
		treeViewLayers.setRoot(rootTreeItem);
	}

}
