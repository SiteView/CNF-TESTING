	/***********************************************************************************************
	* return an array of string arrays. Each of these arrays contains the values of all of the
	* inputs whose names are in the inputs array
	***********************************************************************************************/
	function getPropsFromInputs(arrInputIds){
	    // get the number of inputs
		var props = new Array();
		for (i=0; ; i++){
		    var fields = new Array();
            // add the value of each of the inputs to the fields array
		    for (j=0; j<arrInputIds.length; j++){
                var input = document.getElementById(arrInputIds[j] + i);
                if ((j==0) && (input == null)){
                    return props;
                }
                fields.push(input.value);
            }
			props.push(fields);
		}
		return props;
	}

	/***********************************************************************************************
	* get the selected value from a group of radio inputs.
	***********************************************************************************************/
	function getSelectedInputs(documentObj, prefix){
		var selections = new Array();
		for (i=0; ; i++){
			var input = documentObj.getElementById(prefix + i);
			if (input == null){
			    if (i == 0){
			        return null;
			    }else{
			        break;
                }
			}
			if (input.checked){
				var name = input.value;
				var description = input.title;
				selections.push(new Array(name, description));
			}
		}
		return selections;
	}


	/***********************************************************************************************
	* get the input as a url param.
	***********************************************************************************************/
	function getInputAsUrlParam(name){
        var input = getInputByName(name);
        return (input == null) ? "" : input.name + "=" + input.value;
	}

	/***********************************************************************************************
	* get the input that matches the requested name
	***********************************************************************************************/
	function getInputByName(name){
	    var elements = document.getElementByName(name);
        for (i=0; elements != null && i<elements.length; i++){
            if (elements[i].tagName.toLowerCase() == "input"){
                return elements[i];
            }
	    }
	    return null;
    }

	/***********************************************************************************************
	* check or clear all checkboxes that are identified by the specified prefix
	***********************************************************************************************/
    function checkCheckboxes(prefix, check){
        for (i=0; ;i++){
            var checkbox = document.getElementById(prefix+i);
            if (checkbox == null){
                return;
            }
            checkbox.checked = check;
        }
    }

