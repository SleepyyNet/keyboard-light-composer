package org.enoy.klc.app.components.tree;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.*;
import org.enoy.klc.common.layers.EffectGroupLayer;
import org.enoy.klc.common.layers.EffectLayer;
import org.enoy.klc.common.layers.LayerBase;
import org.enoy.klc.control.utils.DelayedExecuter;
import org.enoy.klc.control.utils.LayerBaseUtil;
import org.enoy.klc.control.utils.ListItemUtil;

import java.util.HashMap;
import java.util.Map;

public class LayerBaseTreeCell extends TreeCell<LayerBaseContainer<? extends LayerBase>> {

	public static final DataFormat LAYER_DATA_FORMAT = new DataFormat(LayerBase.class.getName());
	private static final String DRAG_BELOW = "drag-below-line";
	private static final String DRAG_ABOVE = "drag-above-line";
	private static final String DRAG_INSIDE = "drag-inside";

	public LayerBaseTreeCell() {

		setOnDragOver(this::onDragOver);
		setOnDragDetected(this::onDragDetected);
		setOnDragExited(this::onDragExited);
		setOnDragDropped(this::onDragDropped);
		setOnDragDone(this::onDragDone);

		getStyleClass().add("fa");
	}

	@Override
	protected void updateItem(LayerBaseContainer<? extends LayerBase> item, boolean empty) {
		super.updateItem(item, empty);

		if (!empty && item != null) {
			LayerBase layerBase = item.getLayerBase();

			String name = "-";

			if (layerBase instanceof EffectLayer) {
				EffectLayer effectLayer = (EffectLayer) layerBase;
				name = "\uf005 ";
				name += effectLayer.getEffectLayerInformation().getName().getValue();
			} else if (layerBase instanceof EffectGroupLayer) {
				EffectGroupLayer effectGroupLayer = (EffectGroupLayer) layerBase;
				name = "\uf07b ";
				name += effectGroupLayer.getEffectLayerInformation().getName().getValue();
			}

			setText(name);
		} else {
			setGraphic(null);
			setText(null);
		}

	}

	private void onDragDetected(MouseEvent event) {
		LayerBaseContainer<? extends LayerBase> layerBaseContainer = getItem();

		if (layerBaseContainer != null) {
			LayerBase layerBase = layerBaseContainer.getLayerBase();

			Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
			dragboard.setDragView(snapshot(null, null), 10, 10);

			Map<DataFormat, Object> data = new HashMap<>();
			String name = LayerBaseUtil.getName(layerBase);

			data.put(LAYER_DATA_FORMAT, name);
			data.put(DataFormat.PLAIN_TEXT, name);

			LayerTreeCellDragboard.setCurrentlyDraggedLayerTreeItem(getTreeItem());

			dragboard.setContent(data);
		}

		event.consume();
	}

	private void onDragOver(DragEvent event) {
		LayerBaseContainer<? extends LayerBase> layerBaseContainer = getItem();

		clearDragStyles();
		if (layerBaseContainer != null) {
			if (isLayerPresentInDragboard(event.getDragboard())
					&& !LayerTreeCellDragboard.isLayerDragged(getTreeItem())) {

				double y = event.getY();
				double height = getHeight();

				if (layerBaseContainer.getLayerBase() instanceof EffectGroupLayer) {
					String style = y < height / 4 ? DRAG_ABOVE : y < height * 3 / 4 ? DRAG_INSIDE : DRAG_BELOW;
					getStyleClass().add(style);
				} else {
					boolean above = y < getHeight() / 2;
					getStyleClass().add(above ? DRAG_ABOVE : DRAG_BELOW);
				}
			}
		} else {
			// over empty treecell -> treeview root
			getTreeView().getStyleClass().add(DRAG_INSIDE);
		}

		event.acceptTransferModes(TransferMode.MOVE);
		event.consume();
	}

	private void onDragExited(DragEvent event) {
		clearDragStyles();
		event.consume();
	}

	private void onDragDropped(DragEvent event) {
		TreeItem<LayerBaseContainer<? extends LayerBase>> currentlyDraggedLayerTreeItem = LayerTreeCellDragboard
				.getCurrentlyDraggedLayerTreeItem();

		if (currentlyDraggedLayerTreeItem != null) {
			LayerBaseContainer<? extends LayerBase> targetItem = getItem();

			if (targetItem != null) {
				droppedOnTreeItem(event, currentlyDraggedLayerTreeItem, targetItem);
			} else {
				droppedOnEmptyTreeItem(currentlyDraggedLayerTreeItem);
			}

		}

		event.consume();

	}

	private void droppedOnEmptyTreeItem(
			TreeItem<LayerBaseContainer<? extends LayerBase>> currentlyDraggedLayerTreeItem) {

		TreeItem<LayerBaseContainer<? extends LayerBase>> draggedParent = currentlyDraggedLayerTreeItem.getParent();
		ObservableList<TreeItem<LayerBaseContainer<? extends LayerBase>>> draggedParentChildren = draggedParent != null
				? draggedParent.getChildren() : null;
		ObservableList<TreeItem<LayerBaseContainer<? extends LayerBase>>> rootChildren = this.getTreeView().getRoot()
				.getChildren();

		if (draggedParentChildren != null) {
			draggedParentChildren.remove(currentlyDraggedLayerTreeItem);
		} 
		
		DelayedExecuter.execute(100, () -> rootChildren.add(currentlyDraggedLayerTreeItem));

	}

	private void droppedOnTreeItem(DragEvent event,
			TreeItem<LayerBaseContainer<? extends LayerBase>> currentlyDraggedLayerTreeItem,
			LayerBaseContainer<? extends LayerBase> targetItem) {
		LayerBase targetLayer = targetItem.getLayerBase();

		double y = event.getY();
		double height = getHeight();
		boolean above = y < height / 2;

		TreeItem<LayerBaseContainer<? extends LayerBase>> targetParent = this.getTreeItem().getParent();
		ObservableList<TreeItem<LayerBaseContainer<? extends LayerBase>>> targetParentChildren = targetParent
				.getChildren();

		TreeItem<LayerBaseContainer<? extends LayerBase>> draggedItem = currentlyDraggedLayerTreeItem;
		TreeItem<LayerBaseContainer<? extends LayerBase>> draggedParent = draggedItem.getParent();
		ObservableList<TreeItem<LayerBaseContainer<? extends LayerBase>>> draggedParentChildren = draggedParent != null
				? draggedParent.getChildren() : null;

		if (targetLayer instanceof EffectGroupLayer && (y >= height / 4 && y < height * 3 / 4)) {
			// inside group
			ObservableList<TreeItem<LayerBaseContainer<? extends LayerBase>>> groupChildren = getTreeItem()
					.getChildren();
			if (!groupChildren.equals(draggedParentChildren)) {
				if (draggedParentChildren != null) {
					draggedParentChildren.remove(draggedItem);
				}
				DelayedExecuter.execute(100, () -> groupChildren.add(0, draggedItem));
			}
		} else {
			if (draggedParentChildren != null) {
				ListItemUtil.swapItemsJavaFxThread(draggedParentChildren, targetParentChildren, draggedItem, this.getTreeItem(),
						above);
			} else {
				ListItemUtil.insertItem(targetParentChildren, draggedItem, this.getTreeItem(), above);
			}
		}
	}

	private void onDragDone(DragEvent event) {
		LayerTreeCellDragboard.setCurrentlyDraggedLayerTreeItem(null);
		event.consume();
	}

	private boolean isLayerPresentInDragboard(Dragboard dragboard) {
		return dragboard.getContent(LAYER_DATA_FORMAT) != null;
	}

	private void clearDragStyles() {
		getStyleClass().remove(DRAG_BELOW);
		getStyleClass().remove(DRAG_ABOVE);
		getStyleClass().remove(DRAG_INSIDE);
		getTreeView().getStyleClass().remove(DRAG_INSIDE);
	}

}
