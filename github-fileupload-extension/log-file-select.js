/*
 * GitHub File Select Logger Chrome Extension
 *
 * Listens for classic 'user-dialog-based' and 'drag-and-drop' file select 
 * and writes name, last modified date and content of the file to the console 
 *
 * Check out the following links for availability of the APIs
 *    https://developer.mozilla.org/en-US/docs/Web/API/FileList
 *    https://developer.mozilla.org/en-US/docs/Web/API/File
 *    https://developer.mozilla.org/en-US/docs/Web/API/FileReader
 */


/*
 * Takes a FileList object as argument and prints for every contained child
 * some metadata and the file contents.
 */
var logFiles = function(files) {
  $(files).each(function(){

    // First log some file metadata ...
    console.log("--------------------------------------------------------");
    console.log("--------------------------------------------------------");
    console.log("Name:          ", this.name);
    console.log("Last Modified: ", this.lastModified);
    console.log("--------------------------------------------------------");

    // ... the use FileReader to read the file contents and also ...
    var reader = new FileReader();
    reader.readAsBinaryString(this);

    // ... log them to console once reading has finished
    reader.onloadend = function(){
      console.log(reader.result);
    }
  });
}

/*
 * Generic listener for classic user dialog based file select
 */
$("input[type='file'").on("change", function(event) {
    logFiles(this.files)
});

/*
 * Not so generic drag and drop file listener
 * which listens for drop events onto the .js-uploadable-container div
 * which happens to be the dropzone on GitHub's file upload page
 */
$("div.js-uploadable-container").on("drop", function(event) {
  // Btw. jQuery events don't not have a dataTransfer property, but
  // they carry the original JavaScript event which does have it
  logFiles(event.originalEvent.dataTransfer.files)
});