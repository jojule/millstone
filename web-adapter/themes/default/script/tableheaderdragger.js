/*
* Manages draggable table headers.
* variableId: id of the variable (e.g textfield) to update.
* hidName: the name of the element attribute that contains the header id.
*
* Call addDraggableById(elementId) to add elements to participate in d&d.
* note: draggable elements should have position:relative
*/
function TableHeaderDragger(variableId,cidName) {
    this.all = new Array();
    this.variableId = variableId;
    this.cidName = (cidName?cidName:"cid");

    /* Add an element to participate in d&d */
    this.addDraggableById = function(elementId) {
        var o = document.getElementById(elementId);
        o.thd = this;
        var i = this.all.length;
        this.all[i] = o;
        o.next = null;
        if (i>0) o.prev = this.all[i-1];
        if (o.prev) o.prev.next = o;
        o.onmousedown = this.start;
        o.dragclickhandler = o.onclick;
        o.onclick = this.catchClick;
    }
    this.start = function(e) {
        e = (e?e:window.event);
       	var o = (e.srcElement?e.srcElement:e.target);
        o.thd.dragging = o;
        o.thd.moved = false;
        var y = parseInt(o.style.bottom);
        var x = parseInt(o.style.left);

        o.lastMouseX = e.clientX;
        o.lastMouseY = e.clientY;

        o.startx = e.clientX;

        if (o.minX != null)	o.minMouseX	= e.clientX - x + o.minX;
        if (o.maxX != null)	o.maxMouseX	= o.minMouseX + o.maxX - o.minX;
        if (o.minY != null) o.maxMouseY = -o.minY + e.clientY + y;
        if (o.maxY != null) o.minMouseY = -o.maxY + e.clientY + y;

        document.onmousemove	= o.thd.drag;
        document.onmouseup		= o.thd.end;
        document.currentTHD = o.thd;
        return false;
    }
    this.drag = function(e) {
        var thd = document.currentTHD;
        var o = thd.dragging;
        e = thd.fixE(e);

        var ey	= e.clientY;
        var ex	= e.clientX;
        var y = parseInt(o.style.bottom);
        var x = parseInt(o.style.left);
        var nx, ny;

        nx = x + (ex - o.lastMouseX);
        ny = y + (ey - o.lastMouseY);

        if (!thd.moved&&(e.clientX-o.startx)!=0) thd.moved = true;

        o.style["left"] = e.clientX-o.startx;
        o.style["top"] = 10;

        for (i=0;i<thd.all.length;i++) {
            var trg = thd.all[i];
            if (trg!=o&&ex>=trg.offsetLeft&&ex<=trg.offsetLeft+(trg.offsetWidth/2)) {
            	trg.style.borderRight = 'none';
                trg.style.borderLeft = '2px solid white';
            } else if (trg!=o&&ex>=trg.offsetLeft+(trg.offsetWidth/2)&&ex<=trg.offsetLeft+trg.offsetWidth) {
            	trg.style.borderLeft = 'none';
                trg.style.borderRight = '2px solid white';
            } else {
            	trg.style.borderLeft = 'none';
            	trg.style.borderRight='none';
            }
        }


        return false;
    }
    this.end = function(e) {
        var thd = document.currentTHD;
        var o = thd.dragging;
        e = thd.fixE(e);
        document.onmousemove = null;
        document.onmouseup   = null;

        var hit = false;
        for (i=0;i<thd.all.length;i++) {
            var trg = thd.all[i];
            if (trg!=o&&e.clientX>=trg.offsetLeft&&e.clientX<=trg.offsetLeft+(trg.offsetWidth/2)) {
                hit = true;
                // disconnect form old position
                var oprev = o.prev;
                var onext = o.next;
                if (oprev!=null) oprev.next = null;
                if (onext!=null) onext.prev = null;
                o.next = null;
                o.prev = null;
                if (oprev!=null) oprev.next = onext;
                if (onext!=null) onext.prev = oprev;
                // connect at new position
                o.prev = trg.prev;
                if (trg.prev!=null) trg.prev.next = o;
                o.next = trg;
                trg.prev = o;
                break;
            } else if (trg!=o&&e.clientX>=trg.offsetLeft+(trg.offsetWidth/2)&&e.clientX<=trg.offsetLeft+trg.offsetWidth) {
                hit = true;
                // disconnect form old position
                var oprev = o.prev;
                var onext = o.next;
                if (oprev!=null) oprev.next = null;
                if (onext!=null) onext.prev = null;
                o.next = null;
                o.prev = null;
                if (oprev!=null) oprev.next = onext;
                if (onext!=null) onext.prev = oprev;
                // connect at new position
                o.next = trg.next;
                if (trg.next!=null) trg.next.prev = o;
                o.prev = trg;
                trg.next = o;
                break;
            }
            
        }
        o.style["top"] = 0;
        if (!hit) {
            o.style["left"] = 0;
            return;
        }
        var beg = o;
        while (beg.prev != null) {
            beg = beg.prev;
        }
        var list = "";
        while (beg.next!=null) {
            list += beg.attributes[thd.cidName].value + ",";
             beg = beg.next;
        }
        list += beg.attributes[thd.cidName].value;
        document.getElementById(thd.variableId).value = list;

        o = null;
        document.currentTHD = null;
        Millstone.submit();
    }
    this.fixE = function(e) {
        if (typeof e == 'undefined') e = window.event;
        if (typeof e.layerX == 'undefined') e.layerX = e.offsetX;
        if (typeof e.layerY == 'undefined') e.layerY = e.offsetY;
        return e;
    }
    this.catchClick = function(e) {
        e= (e?e:window.event);
        var o = (e.srcElement?e.srcElement:e.target);
        var thd = o.thd;
         if (!thd.moved) {
           if (o.dragclickhandler) o.dragclickhandler(e);
        }
   }


}

