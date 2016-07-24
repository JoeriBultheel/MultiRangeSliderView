MultiRangeSliderView {

	var userView, sliders, >action;

	*new { arg parent,bounds;
		^super.new.init(parent,bounds)
	}

	init { arg parent,bounds;

		sliders = List().add(MultiRangeSlider());

		userView = UserView(parent,bounds)
		.drawFunc_({ |me|

			var thumbHeight = me.bounds.height/sliders.size;

			sliders.do({ arg slider, idx;

				var exactsliderThumbWidth, thumbStartX,
				thumbEndX,thumbStartY, thumbEndY, thumbStartRangeEnd, thumbEndRangeStart;

				//variable initialisation
				exactsliderThumbWidth = slider.thumbWidth * me.bounds.width;
				slider.pixelSpec = [0,me.bounds.width-exactsliderThumbWidth].asSpec;
				thumbStartX = slider.pixelSpec.map(slider.value);
				thumbEndX = thumbStartX + exactsliderThumbWidth;
				thumbStartRangeEnd = thumbStartX + (slider.startRange * exactsliderThumbWidth);
				thumbEndRangeStart = thumbEndX - (slider.endRange * exactsliderThumbWidth);
				thumbStartY = thumbHeight * idx;
				thumbEndY = thumbStartY + thumbHeight;

				slider.lowValue = thumbStartX / me.bounds.width;
				slider.highValue = thumbEndX / me.bounds.width;
				slider.lowRangeMax = thumbStartRangeEnd / me.bounds.width;
				slider.highRangeMin = thumbEndRangeStart / me.bounds.width;

				//global settings
				Pen.width = 2;

				//outer square
				Pen.strokeColor = Color.red;
				Pen.moveTo(thumbStartX@thumbStartY);
				Pen.lineTo(thumbEndX@thumbStartY);
				Pen.lineTo(thumbEndX@thumbEndY);
				Pen.lineTo(thumbStartX@thumbEndY);
				Pen.lineTo(thumbStartX@thumbStartY);
				Pen.stroke;

				//left square
				Pen.width = 0;
				Pen.moveTo(thumbStartX@thumbStartY+2);
				Pen.lineTo(thumbStartRangeEnd@thumbStartY+2);
				Pen.lineTo(thumbStartRangeEnd+4@thumbEndY-2);
				Pen.lineTo(thumbStartX+4@thumbEndY-2);
				Pen.lineTo(thumbStartX@thumbStartY+2);

				//right square
				Pen.moveTo(thumbEndRangeStart-4@thumbStartY+2);
				Pen.lineTo(thumbEndX-4@thumbStartY+2);
				Pen.lineTo(thumbEndX@thumbEndY-2);
				Pen.lineTo(thumbEndRangeStart@thumbEndY-2);
				Pen.lineTo(thumbEndRangeStart-4@thumbStartY+2);

				Pen.fillColor = Color.new(1,0,0,0.4);
				Pen.fill;

				//handles
				Pen.width = 2;
				Pen.moveTo(thumbStartRangeEnd-1@thumbStartY+2);
				Pen.lineTo(thumbStartRangeEnd+3@thumbEndY-2);
				Pen.moveTo(thumbEndRangeStart-2@thumbStartY+2);
				Pen.lineTo(thumbEndRangeStart+2@thumbEndY-2);
				Pen.strokeColor = Color.new(1,165/255,0,0.8);
				Pen.stroke;
			})
		})
		.mouseDownAction_({ |me,x,y,mod|

			this.eventHandler(me,x,y,mod);
		})
		.mouseMoveAction_({ |me,x,y,mod|

			this.eventHandler(me,x,y,mod);
		})
	}

	eventHandler { |me,x,y,mod|

		var value, idx;

		//calculate the values
		x = max(0,x.value);
		x = min(me.bounds.width,x.value);
		value = x / me.bounds.width;

		y = max(0,y.value);
		y = min(me.bounds.height,y.value);
		idx = (y / (me.bounds.height/sliders.size)).floor;

		idx = min(sliders.size-1,idx);
		idx = max(0,idx);

		switch (mod.value,
			0,
			{
				//no key sets the value
				sliders[idx].value = value;
			},
			262144,
			{
				//hold cntrl for start-range
				sliders[idx].startRange = value;
				sliders[idx].endRange = min(sliders[idx].endRange,1-sliders[idx].startRange);
			},
			524288,
			{
				//hold alt for end-range
				sliders[idx].endRange = 1.0-value;
				sliders[idx].startRange = min(sliders[idx].startRange,value);
			},
			131072,
			{
				//hold shift for thumbsize
				var absoluteSliderMiddle, newThumbVal, valueTillBorder1, valueTillBorder2;
				absoluteSliderMiddle = sliders[idx].pixelSpec.map(sliders[idx].value)+((sliders[idx].thumbWidth*me.bounds.width)/2);
				newThumbVal = abs(absoluteSliderMiddle - x);
				valueTillBorder1 = absoluteSliderMiddle;
				valueTillBorder2 = me.bounds.width - absoluteSliderMiddle;
				newThumbVal = min(newThumbVal,valueTillBorder1);
				newThumbVal = min(newThumbVal,valueTillBorder2);
				newThumbVal = (newThumbVal * 2) / me.bounds.width;

				sliders[idx].thumbWidth = newThumbVal;
		});

		//do the action
		if (action.notNil, {action.(sliders)});
		me.refresh;
	}

	sliders_ { arg newSliders;
		sliders = newSliders.deepCopy;
		if (action.notNil, {action.(sliders)});
		userView.refresh;
	}

	sliders {
		^sliders;
	}

	sliderValues_ { arg value;
		sliders = List.newUsing(value.collect({arg val; MultiRangeSlider().value = val}));
		userView.refresh;
	}

	background_ { arg color;
		userView.background_(color);
	}

	visible_ { arg bool;
		userView.visible = bool;
	}

	insert { arg idx;
		sliders.insert(idx, MultiRangeSlider());
		userView.refresh;
	}

	removeAt { arg idx;
		var temp = sliders.copy;
		temp.removeAt(idx);
		sliders = temp;
		userView.refresh;
	}

	valueAction_ { arg values;
		if (action.notNil, {action.(values)});
	}
}

MultiRangeSlider {

	var <>value=0.5, <>thumbWidth=0.2, <>startRange=0.0, <>endRange=0.0,
	<>pixelSpec, <>lowValue, <>highValue, <>lowRangeMax, <>highRangeMin;

	*new {
		^super.new.init()
	}

	init {
	}
}
