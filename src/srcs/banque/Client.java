package srcs.banque;

import java.io.*;

import srcs.persistance.Sauvegardable;

public class Client implements Sauvegardable{

	
	private final String nom;
	private final Compte compte;

	
	public Client(String nom, Compte compte) {
		this.nom=nom;
		this.compte=compte;

	}
	
	public Client(InputStream in) throws IOException {
		
		compte=new Compte(in);
		DataInputStream dis=new DataInputStream(in);
		nom=dis.readUTF();
		
	}
	public String getNom() {
		return nom;
	}


	public Compte getCompte() {
		return compte;
	}

	@Override
	public boolean equals(Object o) {
		if(o==this) return true;
		if(o==null) return false;
		if(!(o instanceof Compte)) return false;
		Client other= (Client) o;
		return other.nom.equals(nom);
	}

	@Override
	public void save(OutputStream out) throws IOException {
		// TODO Auto-generated method stub
		compte.save(out);
		DataOutputStream dos=new DataOutputStream(out);
		dos.writeUTF(nom);
		
	}
	
}
