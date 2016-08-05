# PhotoGallery

Photo Gallery app that will show the first 100 images from flickr getrecent API

It uses a RecyclerView Grid Layout will 3 images in a row.  

Upon clicking on one of the images it show the image fullscreen which the user then can swipe left or right to get to the next one or the previous one.

There is an EndlessScrollListener which when the user scrolls to the end of the list will automatically call the flickr api and get the next page of images.

There is also a SwipeRefreshLayout that will refresh the grid list to a fresh 100 images from flickr
