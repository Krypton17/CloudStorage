import React, { Component } from 'react';

import DefaultContextMenu	from './contextmenu/DefaultContextMenu';
import Folder 				from '../elements/folder/Folder';
import File 				from '../elements/file/File';
import App					from '../../../App';
import {FileEntity} 		from '../../../model/entity/FileEntity';
import {FolderEntity} 		from '../../../model/entity/FolderEntity';

type ContentContainerProps = {
	parent: App,
	files: FileEntity[],
	folders: FolderEntity[]
}

export default class ContentContainer extends Component<ContentContainerProps> {
	state = {
		disableContextMenu : false,
		contextMenuShow : false,
		contextMenuStyle : {
			top : '',
			left : ''
		}
	};

	componentDidMount() {
		const div = document.getElementById('content-container');

		if (div !== null) {
			div.addEventListener('contextmenu', e => {
				e.preventDefault();
				this.setState({
					contextMenuShow: true,
					contextMenuStyle: {
						top: e.y - 70,
						left: e.x - 275
					}
				})
			});
		}

		window.addEventListener('click', () => this.setState({ contextMenuShow : false }), false);
	}
	
	createFile = (data: FileEntity) => {
		const mainParent = this.props.parent;

		return (
			<File
				key={data.path}
				data={data}
				mainParent={mainParent}
				parent={this}
				handleAction={(action: string) => {
					mainParent.setState({elementSelected: data});

					mainParent.handleContextMenuAction(action, data);
				}}
			/>
		);
	};

	createFolder = (data: FolderEntity) => {
		const mainParent = this.props.parent;

		return (
			<Folder
				key={data.path}
				data={data}
				mainParent={mainParent}
				parent={this}
				handleAction={(action: string) => mainParent.handleContextMenuAction(action, data)}
				whenClicked={() => 
					mainParent.state.elementSelected !== undefined
					&&
					mainParent.state.elementSelected.id === data.id
							? mainParent.updateFolderInfo(data.id)
							: mainParent.setState({ elementSelected : data})
				}/>
		);
	};

	handleContextMenu = (action: string) => {
		if (action === 'upload-files') {
			const input = document.getElementById("select-upload-files");

			if (input !== null) input.click();
		} else if (action === 'paste') {
			this.props.parent.sendPasteAction(this.props.parent.state.bufferElement);
		}
	};

	render() {
		return (
			<div id="content-container">
				{this.props.children}
				<div className="elements">
					{this.props.folders.map(folder => this.createFolder(folder))}
					{this.props.files.map(file => this.createFile(file))}
				</div>
				{this.state.contextMenuShow && !this.state.disableContextMenu
					? <DefaultContextMenu
						style={this.state.contextMenuStyle}
						parent={this.props.parent}
						action={this.handleContextMenu}
						/>
					: undefined}
			</div>	
		);
	}
}