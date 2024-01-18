package es.upm.etsiinf.pui.pui_newsmanager.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.util.Hashtable;

import org.json.simple.JSONObject;

import es.upm.etsiinf.pui.pui_newsmanager.exceptions.ServerCommunicationError;
import es.upm.etsiinf.pui.pui_newsmanager.util.Utils;

public class Article extends ModelEntity {

	@SerializedName("title")
	private String titleText;

	@SerializedName("category")
	private String category;

	@SerializedName("abstract")
	private String abstractText;

	@SerializedName("body")
	private String bodyText;

	@SerializedName("subtitle")
	private String footerText;

	@SerializedName("id_user")
	private int idUser;

	@SerializedName("image_description")
	private String imageDescription;

	@SerializedName("thumbnail_image")
	private String thumbnail;

	@SerializedName("update_date")
	private String publicationDate;

	@SerializedName("username")
	private String username;

	//Additional attribute for the image. By default is null, but we have to retrieve the image
	@SerializedName("image_data")
	private String imageData;

	private Image mainImage;

	private String parseStringFromJson(JSONObject jsonArticle, String key, String def){
		Object in = jsonArticle.getOrDefault(key,def);
		return (in==null?def:in).toString();
	}

	public Article(ModelManager mm, String category, String titleText, String abstractText, String body, String footer){
		super(mm);
		id = -1;
		this.category = category;
		this.abstractText = abstractText;
		this.titleText = titleText;
		bodyText = body;
		footerText = footer;
	}

	public void setId(int id){
		if (id <1){
			throw new IllegalArgumentException("ERROR: Error setting a wrong id to an article:"+id);
		}
		if (this.id>0 ){
			throw new IllegalArgumentException("ERROR: Error setting an id to an article with an already valid id:"+this.id);
		}
		this.id = id;
	}

	public String getTitleText() {
		return titleText;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category= category;
	}
	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}
	public String getAbstractText() {
		return abstractText;
	}
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}
	public String getBodyText() {
		return bodyText;
	}
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}
	public String getFooterText() {
		return footerText;
	}
	public void setFooterText(String footerText) {
		this.footerText = footerText;
	}

	public int getIdUser(){
		return idUser;
	}

	public String getUsername() {
		return username;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public String getImage() {
		return this.imageData != null ? this.imageData : this.thumbnail;
	}

	public void setImage(Image image) {
		this.mainImage = image;
	}

	public Image addImage(String b64Image, String description) throws ServerCommunicationError{
		int order = 1;
		Image img =new Image(mm, order, description, getId(), b64Image);
		mainImage= img;
		return img;
	}

	@Override
	public String toString() {
		return "Article [id=" + getId()
				//+ "isPublic=" + isPublic + ", isDeleted=" + isDeleted
				+", titleText=" + titleText
				+", abstractText=" + abstractText
				+  ", bodyText="	+ bodyText + ", footerText=" + footerText
				+ ", publicationDate=" + publicationDate
				+", image_description=" + imageDescription
				+", image_data=" + mainImage
				+", thumbnail=" + thumbnail
				+ "]";
	}

	public Hashtable<String,String> getAttributes(){
		Hashtable<String,String> res = new Hashtable<String,String>();
		//res.put("is_public", ""+(isPublic?1:0));
		//res.put("id_user", ""+idUser);
		res.put("category", category);
		res.put("abstract", abstractText);
		res.put("title", titleText);
		//res.put("is_deleted", ""+(isDeleted?1:0));
		res.put("body", bodyText);
		res.put("subtitle", footerText);
		if (mainImage!=null){
			res.put("image_data", mainImage.getImage());
			res.put("image_media_type", "image/png");
		}

		if (mainImage!=null && mainImage.getDescription()!=null && !mainImage.getDescription().isEmpty())
			res.put("image_description", mainImage.getDescription());
		else if (imageDescription!=null && !imageDescription.isEmpty())
			res.put("image_description", imageDescription);

		res.put("publication_date", publicationDate);
		return res;
	}
	public Bitmap getBitmapImage(){
		Bitmap bitmapImage = null;
		String imageStr = this.getImage();

		if(imageStr != null) {
			if (imageStr != null && !"".equals(imageStr)) {
				//We create the Bitmap
				byte[] encondedImage = Base64.decode(imageStr, Base64.DEFAULT);
				bitmapImage = BitmapFactory.decodeByteArray(encondedImage, 0, encondedImage.length);
			}
		}
		return bitmapImage;
	}

}