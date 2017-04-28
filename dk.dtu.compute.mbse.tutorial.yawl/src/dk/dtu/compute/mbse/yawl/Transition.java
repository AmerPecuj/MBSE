/**
 */
package dk.dtu.compute.mbse.yawl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dk.dtu.compute.mbse.yawl.Transition#getSplitType <em>Split Type</em>}</li>
 *   <li>{@link dk.dtu.compute.mbse.yawl.Transition#getJoinType <em>Join Type</em>}</li>
 * </ul>
 *
 * @see dk.dtu.compute.mbse.yawl.YawlPackage#getTransition()
 * @model
 * @generated
 */
public interface Transition extends org.pnml.tools.epnk.pnmlcoremodel.Transition {
	/**
	 * Returns the value of the '<em><b>Split Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Split Type</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Split Type</em>' containment reference.
	 * @see #setSplitType(TransitionType)
	 * @see dk.dtu.compute.mbse.yawl.YawlPackage#getTransition_SplitType()
	 * @model containment="true"
	 * @generated
	 */
	TransitionType getSplitType();

	/**
	 * Sets the value of the '{@link dk.dtu.compute.mbse.yawl.Transition#getSplitType <em>Split Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Split Type</em>' containment reference.
	 * @see #getSplitType()
	 * @generated
	 */
	void setSplitType(TransitionType value);

	/**
	 * Returns the value of the '<em><b>Join Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Join Type</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Join Type</em>' containment reference.
	 * @see #setJoinType(TransitionType)
	 * @see dk.dtu.compute.mbse.yawl.YawlPackage#getTransition_JoinType()
	 * @model containment="true"
	 * @generated
	 */
	TransitionType getJoinType();

	/**
	 * Sets the value of the '{@link dk.dtu.compute.mbse.yawl.Transition#getJoinType <em>Join Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Join Type</em>' containment reference.
	 * @see #getJoinType()
	 * @generated
	 */
	void setJoinType(TransitionType value);

} // Transition
