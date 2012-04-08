/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

class testAction extends COM.dragonflow.SiteView.Action {

    testAction() {
        runType = 1;
    }

    public boolean execute() {
        for (int i = 0; i < 10; i++) {
            getElement("test 1");
        }

        return true;
    }

    public String toString() {
        return "test event";
    }
}
