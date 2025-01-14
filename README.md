## Image Comparison    


1. Implementation uses only standard JDK features, no 3rd party libraries are
   permitted.    
2. Pixels (with the same coordinates in two images) can be visually similar, but have
   different values of RGB. We should only mark pixel as "different" if difference between
   them is more than 10%.
3. Differences can be shown as a generated output image with different regions highlighted
   in red.
4. Differences are grouped into rectangles so that the user can easily see them on
   the output image.
5. A “tolerance” comparison parameter allows to treat similar colors as the
   same (2 shades of bright red with less than 10% distance are not shown as differences,
   for example).
6. It should be possible to exclude certain parts of the image from comparison, for example
   a clock or dynamically generated number. They will be provided by the caller as a list of
   rectangles to exclude.
