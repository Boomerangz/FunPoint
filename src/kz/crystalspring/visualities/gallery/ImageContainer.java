package kz.crystalspring.visualities.gallery;

import java.util.List;

import kz.crystalspring.funpoint.venues.UrlDrawable;

public interface ImageContainer
{
	public int getPhotosCount();
	public UrlDrawable getUrlAndPhoto(int i);
	public int hashCode();
}
