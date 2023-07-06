package com.lessnop.customevents.data;

import com.lessnop.customevents.CustomEvents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SimpleFile {

	protected File file;

	public SimpleFile(String fileName) {
		CustomEvents main = CustomEvents.getInstance();
		this.file = new File(main.getDataFolder(), fileName);
		if (!this.file.exists()) {
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				InputStream inputStream = CustomEvents.class.getClassLoader().getResourceAsStream(fileName);
				byte[] lineBuffer = new byte[4096];
				int lineLength;
				while ((lineLength = inputStream.read(lineBuffer)) > 0) {
					fileOutputStream.write(lineBuffer, 0, lineLength);
				}
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
