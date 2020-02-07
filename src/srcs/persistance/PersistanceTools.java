package srcs.persistance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import srcs.banque.*;

public class PersistanceTools {

	public static void saveArrayInt(String f,int tab[]) throws IOException {
		
		DataOutputStream os= new DataOutputStream(new FileOutputStream(f));
			
			for(int i : tab) {
				os.writeInt(i);
			}
		os.close();
	}
	
	public static int[] loadArrayInt(String fichier) throws IOException {
		
	
		List<Integer> ar=new ArrayList<>();
		DataInputStream in=new DataInputStream(new FileInputStream(fichier));
		int ret;
		try {
			while((ret= in.readInt()) != -1) {
		
				ar.add(ret);
		}
		}catch(EOFException e) {
			System.out.println("END OF FILE REACHED");
		}
		finally{
			in.close();
		}
		int [] tab = new int[ar.size()];
		for(int j=0; j<ar.size(); j++) {
			tab[j] = ar.get(j);
		}
		
		return tab;
	}
	
	public static void saveCompte(String f,Compte e) throws IOException {
		
		OutputStream os= new FileOutputStream(f);
		e.save(os);
	}
	
	public static Compte loadCompte(String f) throws IOException {
		
		InputStream is= new FileInputStream(f);
		return new Compte(is);
	}
	
	public static void save(String fichier, Sauvegardable s) throws IOException {
		DataOutputStream dos=new DataOutputStream(new FileOutputStream(fichier));
		dos.writeUTF(s.getClass().getCanonicalName());
		s.save(dos);
	}
	
	public static Sauvegardable load(String s) throws ReflectiveOperationException, IOException {
		
		DataInputStream fis=new DataInputStream( new FileInputStream(s));
		String name= fis.readUTF();
		
		Class<? extends Sauvegardable > c=Class.forName(name).asSubclass(Sauvegardable.class);
		return c.getConstructor(InputStream.class).newInstance(fis);
	}
}