/*
 *  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes-Benz Tech Innovation GmbH - Initial API and Implementation
 *       Mercedes-Benz Tech Innovation GmbH - Right value of constraint can now contain iterable of BPNs
 *
 */

package net.catenax.edc.validation.businesspartner.functions;

import java.util.Map;
import java.util.Objects;
import org.eclipse.dataspaceconnector.policy.model.Operator;
import org.eclipse.dataspaceconnector.spi.agent.ParticipantAgent;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.policy.PolicyContext;

/**
 * Abstract class for BusinessPartnerNumber validation. This class may be inherited from the EDC
 * policy enforcing functions for duties, permissions and prohibitions.
 */
public abstract class AbstractBusinessPartnerValidation {

  // Developer Note:
  // Problems reported to the policy context are not logged. Therefore, everything
  // that is reported to the policy context should be logged, too.

  private static final String SKIP_EVALUATION_BECAUSE_ITERABLE_VALUE_NOT_STRING_S =
      "Skipping evaluation of iterable value in BusinessPartnerNumber constraint. Right values used in an iterable must be of type 'String'. Unsupported Type: '%s'";
  private static final String FAIL_EVALUATION_BECAUSE_RIGHT_VALUE_NOT_STRING_S =
      "Failing evaluation because of invalid BusinessPartnerNumber constraint. For operator 'EQ' right value must be of type 'String'. Unsupported Type: '%s'";
  private static final String FAIL_EVALUATION_BECAUSE_RIGHT_VALUE_NOT_ITERABLE_S =
      "Failing evaluation because of invalid BusinessPartnerNumber constraint. For operator 'IN' right value must be of type 'Iterable'. Unsupported Type: '%s'";
  private static final String FAIL_EVALUATION_BECAUSE_UNSUPPORTED_OPERATOR_S =
      "Failing evaluation because of invalid BusinessPartnerNumber constraint. As operator only 'EQ' or 'IN' are supported. Unsupported operator: '%s'";

  private final Monitor monitor;

  protected AbstractBusinessPartnerValidation(Monitor monitor) {
    this.monitor = Objects.requireNonNull(monitor);
  }

  /**
   * Name of the claim that contains the Business Partner Number.
   *
   * <p><strong>Please note:</strong> At the time of writing (April 2022) the business partner
   * number is part of the 'referringConnector' claim in the IDS DAT token. This will probably
   * change for the next release.
   */
  private static final String REFERRING_CONNECTOR_CLAIM = "referringConnector";

  /**
   * Evaluation funtion to decide whether a claim belongs to a specific business partner.
   *
   * @param operator operator of the constraint
   * @param rightValue right value fo the constraint, that contains the business partner number
   *     (e.g. BPNLCDQ90000X42KU)
   * @param policyContext context of the policy with claims
   * @return true if claims are from the constrained business partner
   */
  protected boolean evaluate(
      final Operator operator, final Object rightValue, final PolicyContext policyContext) {

    if (policyContext.hasProblems() && policyContext.getProblems().size() > 0) {
      String problems = String.join(", ", policyContext.getProblems());
      String message =
          String.format(
              "BusinessPartnerNumberValidation: Rejecting PolicyContext with problems. Problems: %s",
              problems);
      monitor.debug(message);
      return false;
    }

    final ParticipantAgent participantAgent = policyContext.getParticipantAgent();
    final Map<String, String> claims = participantAgent.getClaims();

    if (!claims.containsKey(REFERRING_CONNECTOR_CLAIM)) {
      return false;
    }

    String referringConnectorClaim = claims.get(REFERRING_CONNECTOR_CLAIM);
    if (referringConnectorClaim == null || referringConnectorClaim.isEmpty()) {
      return false;
    }

    if (operator == Operator.EQ) {
      return isBusinessPartnerNumber(referringConnectorClaim, rightValue, policyContext);
    } else if (operator == Operator.IN) {
      return containsBusinessPartnerNumber(referringConnectorClaim, rightValue, policyContext);
    } else {
      final String message =
          String.format(FAIL_EVALUATION_BECAUSE_UNSUPPORTED_OPERATOR_S, operator);
      monitor.warning(message);
      policyContext.reportProblem(message);
      return false;
    }
  }

  /**
   * @param referingConnectorClaim of the participant
   * @param businessPartnerNumber object
   * @return true if object is an iterable and constains a string that is successfully evaluated
   *     against the claim
   */
  private boolean containsBusinessPartnerNumber(
      String referingConnectorClaim, Object businessPartnerNumbers, PolicyContext policyContext) {
    if (businessPartnerNumbers == null) {
      final String message =
          String.format(FAIL_EVALUATION_BECAUSE_RIGHT_VALUE_NOT_ITERABLE_S, "null");
      monitor.warning(message);
      policyContext.reportProblem(message);
      return false;
    }
    if (!(businessPartnerNumbers instanceof Iterable)) {
      final String message =
          String.format(
              FAIL_EVALUATION_BECAUSE_RIGHT_VALUE_NOT_ITERABLE_S,
              businessPartnerNumbers.getClass().getName());
      monitor.warning(message);
      policyContext.reportProblem(message);
      return false;
    }

    for (Object businessPartnerNumber : (Iterable) businessPartnerNumbers) {
      if (businessPartnerNumber == null) {
        final String message =
            String.format(SKIP_EVALUATION_BECAUSE_ITERABLE_VALUE_NOT_STRING_S, "null");
        monitor.warning(message);
        policyContext.reportProblem(message);
        continue;
      }
      if (!(businessPartnerNumber instanceof String)) {
        final String message =
            String.format(
                SKIP_EVALUATION_BECAUSE_ITERABLE_VALUE_NOT_STRING_S,
                businessPartnerNumber.getClass().getName());
        monitor.warning(message);
        policyContext.reportProblem(message);
        continue;
      }
      if (isCorrectBusinessPartner(referingConnectorClaim, (String) businessPartnerNumber)) {
        return true; // iterable does contain at least one matching value
      }
    }

    return false;
  }

  /**
   * @param referingConnectorClaim of the participant
   * @param businessPartnerNumber object
   * @return true if object is string and successfully evaluated against the claim
   */
  private boolean isBusinessPartnerNumber(
      String referingConnectorClaim, Object businessPartnerNumber, PolicyContext policyContext) {
    if (businessPartnerNumber == null) {
      final String message =
          String.format(FAIL_EVALUATION_BECAUSE_RIGHT_VALUE_NOT_STRING_S, "null");
      monitor.warning(message);
      policyContext.reportProblem(message);
      return false;
    }
    if (!(businessPartnerNumber instanceof String)) {
      final String message =
          String.format(
              FAIL_EVALUATION_BECAUSE_RIGHT_VALUE_NOT_STRING_S,
              businessPartnerNumber.getClass().getName());
      monitor.warning(message);
      policyContext.reportProblem(message);
      return false;
    }

    return isCorrectBusinessPartner(referingConnectorClaim, (String) businessPartnerNumber);
  }

  /**
   * At the time of writing (11. April 2022) the business partner number is part of the
   * 'referingConnector' claim, which contains a connector URL. As the CX projects are not further
   * alligned about the URL formatting, the enforcement can only be done by checking whether the URL
   * _contains_ the number. As this introduces some insecurities when validation business partner
   * numbers, this should be addresses in the long term.
   *
   * @param referingConnectorClaim describing URL with business partner number
   * @param businessPartnerNumber of the constraint
   * @return true if claim contains the business partner number
   */
  private static boolean isCorrectBusinessPartner(
      String referingConnectorClaim, String businessPartnerNumber) {
    return referingConnectorClaim.contains(businessPartnerNumber);
  }
}
