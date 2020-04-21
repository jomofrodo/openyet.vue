export function jqSubmit(action, fd, fLoad, flgSync, fErr) {
  if (!fLoad)
    fLoad = function(data, strStatus, xhr) {
      //console.log(data);
      return data;
    };
  if (!fErr)
    fErr = function(xhr, strStatus, errMsg) {
      let errMsgx = xhr.responseText ? xhr.responseText : errMsg;
      console.error('Error: ' + errMsgx);
      throw new Error(errMsgx);
    };
  return new Promise(function(fLoad, fErr) {
    $.ajax({
      url: action,
      data: fd,
      sync: flgSync,
      processData: false,
      contentType: false,
      type: 'POST'
    })
      .done(fLoad)
      .fail(fErr);
  });
}