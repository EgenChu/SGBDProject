package SGBD;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HeapFile {

	private RelDef reldef;

	public HeapFile(RelDef reldef) {
		this.reldef = reldef;
	}

	public RelDef getReldef() {
		return reldef;
	}

	public void createNewOndisk() throws IOException {
		PageId temp;
		DiskManager.getInstance().createFile(reldef.getFileIdx());
		temp = DiskManager.getInstance().addPage(reldef.getFileIdx());
		
		for (int i = 0; i < Constants.PAGE_SIZE; i += Integer.BYTES) {
			BufferManager.getInstance().getPage(temp).putInt(i, 0);
		}
		
		BufferManager.getInstance().freePage(temp, true);
	}

	public PageId addDataPage() throws IOException {
		ByteBuffer bf;
		int totalpage;
		
		PageId headerPage = new PageId(reldef.getFileIdx() , 0);
		bf = BufferManager.getInstance().getPage(headerPage);
		
		totalpage = bf.getInt(0) + 1;
		bf.putInt(0, totalpage);
		bf.putInt(totalpage * Integer.BYTES, reldef.getSlotCount());
		
		BufferManager.getInstance().freePage(headerPage, true);
		
		return DiskManager.getInstance().addPage(reldef.getFileIdx());
	}
	
	public PageId getFreeDataPageId() {
		ByteBuffer bf;
		int i = 4;
		int pagelibre = 1, totalpage;
		
		PageId headerPage = new PageId(reldef.getFileIdx(), 0);
		bf = BufferManager.getInstance().getPage(headerPage);
		
		totalpage = bf.getInt(0);
		while(bf.getInt(i) == 0 && pagelibre <= totalpage) {
			i += Integer.BYTES;
			pagelibre++;
		}
		
		BufferManager.getInstance().freePage(headerPage, false);
		
		if( pagelibre > totalpage) return null;
		else 
			return new PageId(reldef.getFileIdx(),pagelibre);
	}
	
	
	
}
