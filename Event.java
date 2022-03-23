import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import java.util.Calendar;

public class Event implements Comparable<Event>{
	
	private String title, description;
	private boolean isPeriod, present;
	private Date date, date2;
	private static Date today;
	private Color color;
	private String hex;
	private String category, alignment;
	private DLList<String> tags;
	private DLList<MyImage> images;
	
	private final Font normalFont = new Font("Ubuntu", Font.PLAIN, 14);
	private final Font boldFont = new Font("Ubuntu", Font.BOLD, 14);
	private final Font italicFont = new Font("Ubuntu", Font.ITALIC, 14);
	private final Font boldItalicFont = new Font("Ubuntu", Font.BOLD + Font.ITALIC, 14);
		
	Calendar calendar = Calendar.getInstance();
	
	//blank event/period
	public Event(boolean isPeriod){
		title = "New " + (isPeriod ? "period" : "event");
		description = "";
		date = new Date(1, 1, 1);
		this.isPeriod = isPeriod;
		if (isPeriod)
			date2 = new Date(1, 1, 2);
		else
			alignment = "Centered";
		color = new Color(128, 128, 128);
		hex = RGBtoHex(color.getRed(), color.getGreen(), color.getBlue());
		category = "<category>";
		tags = new DLList<String>();
		images = new DLList<MyImage>();
		
		today = new Date(calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
	}
	
	//standard event with separate dates and a defined color
	public Event(String title, String description, int month, int day, int year, int red, int green, int blue, String category, String alignment, DLList<String> tags, DLList<MyImage> images){
		this.title = title;
		this.description = description;
		date = new Date(month, day, year);
		isPeriod = false;
		color = new Color(red, green, blue);
		hex = RGBtoHex(red, green, blue);
		this.category = category;
		this.alignment = alignment;
		// this.tags = setTags(tags);
		this.tags = new DLList<String>();
		for (int i = 0; i < tags.size(); i++)
			this.tags.add(tags.get(i));
		this.images = new DLList<MyImage>();
		for (int i = 0; i < images.size(); i++)
			this.images.add(images.get(i));
		
		today = new Date(calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
	}
	
	//standard period with separate dates and a defined color
	public Event(String title, String description, int month, int day, int year, int month2, int day2, int year2, int red, int green, int blue, String category, DLList<String> tags, DLList<MyImage> images){
		this.title = title;
		this.description = description;
		date = new Date(month, day, year);
		date2 = new Date(month2, day2, year2);
		isPeriod = true;
		color = new Color(red, green, blue);
		hex = RGBtoHex(red, green, blue);
		this.category = category;
		// this.tags = setTags(tags);
		this.tags = new DLList<String>();
		for (int i = 0; i < tags.size(); i++)
			this.tags.add(tags.get(i));
		this.images = new DLList<MyImage>();
		for (int i = 0; i < images.size(); i++)
			this.images.add(images.get(i));
		
		today = new Date(calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
	}
	
	//period with separate dates, a defined color, and with the end date set to the present day
	public Event(String title, String description, int month, int day, int year, boolean present, int red, int green, int blue, String category, DLList<String> tags, DLList<MyImage> images){
		this.title = title;
		this.description = description;
		date = new Date(month, day, year);
		isPeriod = true;
		color = new Color(red, green, blue);
		hex = RGBtoHex(red, green, blue);
		this.category = category;
		this.present = true;
		// this.tags = setTags(tags);
		this.tags = new DLList<String>();
		for (int i = 0; i < tags.size(); i++)
			this.tags.add(tags.get(i));
		this.images = new DLList<MyImage>();
		for (int i = 0; i < images.size(); i++)
			this.images.add(images.get(i));
		
		today = new Date(calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
	}
	
	public String getTitle(){ return title; }
	public Date getDate(){ return date; }
	public Date getDate2(){ return date2; }
	public String getDescription(){ return description; }
	public int getMonth(){ return date.getMonth(); }
	public int getDay(){ return date.getDay(); }
	public int getYear(){ return date.getYear(); }
	public int getMonth2(){ return date2.getMonth(); }
	public int getDay2(){ return date2.getDay(); }
	public int getYear2(){ return date2.getYear(); }
	public boolean getIsPeriod(){ return isPeriod; }
	public boolean isPresent(){ return present; }
	public Color getColor(){ return color; }
	public String getHex(){ return hex; }
	public String getCategory(){ return category; }
	public String getAlignment(){ return alignment; }
	public static Date today(){ return today; }
	public DLList<String> getTags(){ return tags; }
	public DLList<MyImage> getImages(){ return images; }
	
	public String getTagString(){
		String s = "";
		if (tags.size() == 0)
			return "none";
		
		for (int i = 0; i < tags.size(); i++)
			s += tags.get(i) + ", ";
		
		return s.substring(0, s.length()-2);
	}
	
	public String toString(){
		String s = date.monthString() + " " + date.getDay() + ", " + date.getYear();
		if (isPeriod){
			s += " - " + date2.monthString() + " " + date2.getDay() + ", " + date2.getYear();
		}
		s += "\n" + title + "\n\n" + description;
		return s;
	}
	
	public String toString(int currentYear, boolean modernDating){
		String s = date.longForm(currentYear, modernDating);
		if (isPeriod)
			s += " - " + (present ? "present" : (date2.longForm(currentYear, modernDating) + " : " + Date.dateDiff(date, date2)));
		if (tags.size() > 0){
			s += "\nTags: ";
			for (int i = 0; i < tags.size(); i++){
				s += tags.get(i) + ", ";
			}
			s = s.substring(0, s.length() - 2);
		}
		
		return s + "\n" + title + "\n\n" + description;
	}
	
	public String toStringVerbose(){
		String s = "";
		if (title.charAt(title.length()-1) == '\n')
			title = title.substring(0, title.length()-1);
		if (description.length() > 0 && description.charAt(description.length()-1) == '\n')
			description = description.substring(0, description.length()-1);
		s += "Title: " + title + "\n";
		s += "Description: " + description + "\n";
		s += "isPeriod: " + isPeriod + "\n";
		s += "Date: " + date.shortForm();
		if (isPeriod)
			s += " - " + (present ? "present\n" : (date2.shortForm() + "\n"));
		else
			s += "\n";
		s += "Color: " + color.getRed() + "/" + color.getGreen() + "/" + color.getBlue() + "\n";
		s += "Category: " + category + "\n";
		s += "Alignment: " + alignment + "\n";
		s += "Tags: " + getTagString() + "\n";
		s += "Images: ";
		for (int i = 0; i < images.size(); i++)
			s += images.get(i).toString() + " | ";
		if (images.size() > 0)
			s = s.substring(0, s.length()-2);
		else
			s += "none";
		
		return s;
	}
	
	@Override
	public boolean equals(Object o){
		Event e = (Event)o;
		return title.equals(e.getTitle()) && date.equals(e.getDate());
	}
	
	@Override
	public int compareTo(Event e){
		if (date.compareTo(e.getDate()) == 0)
			return title.compareTo(e.getTitle());
		return date.compareTo(e.getDate());
	}
	
	public static String RGBtoHex(int red, int green, int blue){
		char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		String hex = "#";
		hex += digits[red / 16];
		hex += digits[red % 16];
		hex += digits[green / 16];
		hex += digits[green % 16];
		hex += digits[blue / 16];
		hex += digits[blue % 16];
		
		return hex;
	}
	
	public static int[] HextoRGB(String hex){
		char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		int[] rgbColors = new int[3];
		if (hex.charAt(0) == '#')
			hex = hex.substring(1);
		for (int i = 0; i < hex.length(); i++){
			for (int j = 0; j < digits.length; j++){
				char hexChar = ("abcdef".contains(""+hex.charAt(i))) ? Character.toUpperCase(hex.charAt(i)) : hex.charAt(i);
				if (hexChar == digits[j]){
					rgbColors[i / 2] += (i % 2 == 0) ? 16*j : j;
					break;
				}
			}
		}
		
		return rgbColors;
	}
	
	private DLList<String> setTags(DLList<String> localTags){
		DLList<String> newTags = new DLList<String>();
		if (localTags == null)
			return newTags;
		
		for (int i = 0; i < localTags.size(); i++){
			newTags.add(localTags.get(i));
		}
		
		return newTags;
	}
	
	public boolean addTag(String newTag){
		if (tags.size() == 0){
			tags.add(newTag);
			return true;
		}
		
		for (int i = 0; i < tags.size(); i++){
			if (tags.get(i).equals(newTag))
				return false;
			if (tags.get(i).compareTo(newTag) < 0){
				tags.add(i, newTag);
				return true;
			}
		}
		
		tags.add(newTag);
		return true;
	}
	
	public void drawString(Graphics g, int x, int y){
		if (title.contains("<b>") && title.contains("</b>") && title.contains("<i>") && title.contains("</i>")){
			title = title.substring(title.indexOf("<b>")+3, title.indexOf("</b>"));
			String boldtitle1 = title.substring(0, title.indexOf("<i>"));
			String boldItalictitle = title.substring(title.indexOf("<i>")+3, title.indexOf("</i>"));
			String boldtitle2 = title.substring(title.indexOf("</i>")+4);
			
			g.setFont(boldFont);
			g.drawString(boldtitle1, x, y);
			
			g.setFont(boldItalicFont);
			g.drawString(boldItalictitle, x + g.getFontMetrics().stringWidth(boldtitle1), y);
			
			g.setFont(boldFont);
			g.drawString(boldtitle2, x + g.getFontMetrics().stringWidth(boldtitle1 + boldItalictitle), y);
		} else if (title.contains("<b>") && title.contains("</b>")){
			String normaltitle1 = title.substring(0, title.indexOf("<b>"));
			String boldtitle = title.substring(title.indexOf("<b>")+3, title.indexOf("</b>"));
			String normaltitle2 = title.substring(title.indexOf("</b>")+4);
			
			g.setFont(normalFont);
			g.drawString(normaltitle1, x, y);
			
			g.setFont(boldFont);
			g.drawString(boldtitle, x + g.getFontMetrics().stringWidth(normaltitle1), y);
			
			g.setFont(normalFont);
			g.drawString(normaltitle2, x + g.getFontMetrics().stringWidth(normaltitle1 + boldtitle), y);
		} else if (title.contains("<i>") && title.contains("</i>")){
			String normaltitle1 = title.substring(0, title.indexOf("<i>"));
			String italictitle = title.substring(title.indexOf("<i>")+3, title.indexOf("</i>"));
			String normaltitle2 = title.substring(title.indexOf("</i>")+4);
			
			g.setFont(normalFont);
			g.drawString(normaltitle1, x, y);
			
			g.setFont(italicFont);
			g.drawString(italictitle, x + g.getFontMetrics().stringWidth(normaltitle1), y);
			
			g.setFont(normalFont);
			g.drawString(normaltitle2, x + g.getFontMetrics().stringWidth(normaltitle1 + italictitle), y);
		} else {
			g.setFont(normalFont);
			g.drawString(title, x, y);
		}
	}
	
	public int formattedLength(Graphics g){
		Font tempFont = g.getFont();
		int formattedStringWidth = 0;
		
		if (title.contains("<b>") && title.contains("</b>") && title.contains("<i>") && title.contains("</i>")){
			String boldtitle1 = title.substring(title.indexOf("<b>")+3, title.indexOf("<i>"));
			String boldItalictitle = title.substring(title.indexOf("<i>")+3, title.indexOf("</i>"));
			String boldtitle2 = title.substring(title.indexOf("</i>")+4, title.indexOf("</b>"));
			
			g.setFont(boldFont);
			formattedStringWidth += g.getFontMetrics().stringWidth(boldtitle1);
			formattedStringWidth += g.getFontMetrics().stringWidth(boldtitle2);
			
			g.setFont(boldItalicFont);
			formattedStringWidth += g.getFontMetrics().stringWidth(boldItalictitle);
		}
		else if (title.contains("<i>") && title.contains("</i>")){
			String normaltitle1 = title.substring(0, title.indexOf("<i>"));
			String italictitle = title.substring(title.indexOf("<i>")+3, title.indexOf("</i>"));
			String normaltitle2 = title.substring(title.indexOf("</i>")+4);
			
			g.setFont(normalFont);
			formattedStringWidth += g.getFontMetrics().stringWidth(normaltitle1);
			formattedStringWidth += g.getFontMetrics().stringWidth(normaltitle2);
			
			g.setFont(italicFont);
			formattedStringWidth += g.getFontMetrics().stringWidth(italictitle);
			
		} else if (title.contains("<b>") && title.contains("</b>")){
			String formattedtitle = title.substring(title.indexOf("<b>")+3, title.indexOf("</b>"));
			g.setFont(boldFont);
			formattedStringWidth = g.getFontMetrics().stringWidth(formattedtitle);
		} else
			formattedStringWidth = g.getFontMetrics().stringWidth(title);
		
		g.setFont(tempFont);
		return formattedStringWidth;
	}
}