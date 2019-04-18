
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dest")
@XmlAccessorType (XmlAccessType.FIELD)
public class Track {

	@XmlElement(name = "dest")
	private String dest;
	@XmlElement(name = "text")
	private String text;
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "Track [dest=" + dest + ", text=" + text + "]";
	}
	
}
