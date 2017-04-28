/**
 */
package dk.dtu.compute.mbse.yawl;

import org.pnml.tools.epnk.pnmlcoremodel.Attribute;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Arc Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dk.dtu.compute.mbse.yawl.ArcType#getArcType <em>Arc Type</em>}</li>
 * </ul>
 *
 * @see dk.dtu.compute.mbse.yawl.YawlPackage#getArcType()
 * @model
 * @generated
 */
public interface ArcType extends Attribute {
	/**
	 * Returns the value of the '<em><b>Arc Type</b></em>' attribute.
	 * The literals are from the enumeration {@link dk.dtu.compute.mbse.yawl.AType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Arc Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Arc Type</em>' attribute.
	 * @see dk.dtu.compute.mbse.yawl.AType
	 * @see #setArcType(AType)
	 * @see dk.dtu.compute.mbse.yawl.YawlPackage#getArcType_ArcType()
	 * @model
	 * @generated
	 */
	AType getArcType();

	/**
	 * Sets the value of the '{@link dk.dtu.compute.mbse.yawl.ArcType#getArcType <em>Arc Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Arc Type</em>' attribute.
	 * @see dk.dtu.compute.mbse.yawl.AType
	 * @see #getArcType()
	 * @generated
	 */
	void setArcType(AType value);

} // ArcType
