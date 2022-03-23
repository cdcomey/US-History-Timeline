import java.awt.Color;

public class Tag implements Comparable<Tag>{
	private String title;
	private Color color;
	
	public Tag(String title){
		this.title = title;
		this.color = new Color(128, 128, 128);
	}
	
	public Tag(String title, Color color){
		this.title = title;
		this.color = color;
	}
	
	public String toString(){ return title + "[r=" + color.getRed() + ",g=" + color.getGreen() + ",b=" + color.getBlue() + "]"; }
	public String getTitle(){ return title; }
	public Color getColor(){ return color; }
	
	public boolean equals(Tag t){ return title.equals(t.getTitle()) && color.equals(t.getColor()); }
	
	@Override
	public int compareTo(Tag t){ return title.compareTo(t.toString()); }
}